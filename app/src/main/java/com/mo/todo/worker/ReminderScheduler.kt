package com.mo.todo.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * 统一的提醒调度器
 * 主要使用 AlarmManager.setAlarmClock() 精确触发（国内 ROM 兼容性最好）
 * 同时设置 WorkManager 作为备用方案
 */
object ReminderScheduler {

    private const val TAG = "ReminderScheduler"

    /**
     * 调度一个待办提醒
     * @param context Application context
     * @param todoId 待办 ID
     * @param title 待办标题
     * @param triggerAtMillis 触发时间（毫秒时间戳）
     */
    fun schedule(context: Context, todoId: Long, title: String, triggerAtMillis: Long) {
        val delay = triggerAtMillis - System.currentTimeMillis()
        if (delay <= 0) {
            Log.w(TAG, "Reminder time is in the past, sending immediately: id=$todoId")
            NotificationHelper.sendReminderNotification(context, todoId, title)
            return
        }

        // 1. AlarmManager - 主要方案（精确闹钟，国内 ROM 兼容性最好）
        scheduleAlarm(context, todoId, title, triggerAtMillis)

        // 2. WorkManager - 备用方案
        scheduleWorkManager(context, todoId, title, delay)

        Log.i(TAG, "Scheduled reminder: id=$todoId, delay=${delay}ms, triggerAt=$triggerAtMillis")
    }

    /**
     * 取消一个待办提醒
     */
    fun cancel(context: Context, todoId: Long) {
        // 取消 AlarmManager
        cancelAlarm(context, todoId)

        // 取消 WorkManager
        WorkManager.getInstance(context).cancelAllWorkByTag("todo_reminder_$todoId")

        Log.i(TAG, "Cancelled reminder: id=$todoId")
    }

    /**
     * 重新调度所有待办提醒（开机后调用）
     */
    suspend fun rescheduleAll(context: Context, database: com.mo.todo.data.database.AppDatabase) {
        try {
            val now = System.currentTimeMillis()
            val upcomingTodos = database.todoDao().getUpcomingRemindersSync(now)
            Log.i(TAG, "Rescheduling ${upcomingTodos.size} reminders after boot")

            upcomingTodos.forEach { todo ->
                todo.reminderTime?.let { time ->
                    if (time > now) {
                        schedule(context, todo.id, todo.title, time)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reschedule reminders: ${e.message}", e)
        }
    }

    // ========== AlarmManager 实现 ==========

    private fun scheduleAlarm(context: Context, todoId: Long, title: String, triggerAtMillis: Long) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pendingIntent = createAlarmPendingIntent(context, todoId, title)

            // setAlarmClock 是最可靠的精确闹钟方式
            // 国内 ROM（MIUI、EMUI、ColorOS 等）对 setAlarmClock 的限制最少
            val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)

            Log.d(TAG, "AlarmManager scheduled: id=$todoId, triggerAt=$triggerAtMillis")
        } catch (e: SecurityException) {
            Log.e(TAG, "AlarmManager security exception (exact alarm permission?): ${e.message}")
            // 如果精确闹钟权限被拒绝，降级到 setExactAndAllowWhileIdle
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val pendingIntent = createAlarmPendingIntent(context, todoId, title)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
                Log.d(TAG, "Fallback to setExact: id=$todoId")
            } catch (e2: Exception) {
                Log.e(TAG, "Fallback alarm also failed: ${e2.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm: ${e.message}", e)
        }
    }

    private fun cancelAlarm(context: Context, todoId: Long) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, todoId.toInt(), intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cancel alarm: ${e.message}", e)
        }
    }

    private fun createAlarmPendingIntent(context: Context, todoId: Long, title: String): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("todo_id", todoId)
            putExtra("todo_title", title)
        }
        return PendingIntent.getBroadcast(
            context, todoId.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // ========== WorkManager 备用方案 ==========

    private fun scheduleWorkManager(context: Context, todoId: Long, title: String, delayMillis: Long) {
        val inputData = Data.Builder()
            .putLong("todo_id", todoId)
            .putString("todo_title", title)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("todo_reminder_$todoId")
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork("reminder_$todoId", ExistingWorkPolicy.REPLACE, workRequest)
    }

    // ========== 定期扫描备用方案（ColorOS 等激进省电 ROM 的终极兜底） ==========

    fun schedulePeriodicCheck(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            15, TimeUnit.MINUTES
        )
            .addTag("periodic_reminder_check")
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "periodic_reminder_check",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )

        Log.i(TAG, "Periodic reminder check scheduled (every 15 min)")
    }
}
