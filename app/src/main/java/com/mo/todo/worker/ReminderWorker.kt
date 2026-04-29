package com.mo.todo.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.mo.todo.MainActivity
import com.mo.todo.data.database.AppDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: AppDatabase
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val CHANNEL_ID = "mo_reminder"
        const val CHANNEL_NAME = "Mo 提醒"
        private const val TAG = "ReminderWorker"
        private const val BOOT_RESCHEDULE_TAG = "mo_boot_reschedule"

        fun createChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttrs = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "待办事项与备忘录提醒通知"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 200, 300)
                    setSound(soundUri, audioAttrs)
                    setShowBadge(true)
                    enableLights(true)
                    lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                    setBypassDnd(true)
                }
                context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
                Log.d(TAG, "Notification channel created: $CHANNEL_ID")
            }
        }

        fun rescheduleAllAfterBoot(context: Context, database: AppDatabase) {
            val workManager = WorkManager.getInstance(context)
            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(androidx.work.Data.Builder().putLong("todo_id", 0L).putString("todo_title", "boot_reschedule").build())
                .addTag(BOOT_RESCHEDULE_TAG)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            workManager.enqueueUniqueWork(BOOT_RESCHEDULE_TAG, ExistingWorkPolicy.REPLACE, workRequest)
            Log.d(TAG, "Boot reschedule worker enqueued")
        }
    }

    override suspend fun doWork(): Result {
        val todoId = inputData.getLong("todo_id", -1L)
        val todoTitle = inputData.getString("todo_title")
        val now = System.currentTimeMillis()
        Log.d(TAG, "doWork() at $now, todoId=$todoId, title=$todoTitle")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "No notification permission")
            return Result.success()
        }

        if (todoId > 0 && todoTitle != null) {
            sendNotification(todoId, todoTitle)
            return Result.success()
        }

        val overdueTodos = database.todoDao().getUpcomingReminders(0, now)
        Log.d(TAG, "Overdue: ${overdueTodos.size}")
        overdueTodos.forEach { sendNotification(it.id, it.title) }
        return Result.success()
    }

    private fun sendNotification(todoId: Long, title: String) {
        try {
            val vibrate = applicationContext.getSharedPreferences("mo_prefs", Context.MODE_PRIVATE)
                .getBoolean("notification_vibrate", true)

            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                applicationContext, todoId.toInt(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText("待办事项提醒")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(soundUri)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            if (vibrate) {
                builder.setVibrate(longArrayOf(0, 300, 200, 300))
            }

            NotificationManagerCompat.from(applicationContext).notify(todoId.toInt(), builder.build())
            Log.i(TAG, "Notification sent: id=$todoId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send notification: ${e.message}", e)
        }
    }
}
