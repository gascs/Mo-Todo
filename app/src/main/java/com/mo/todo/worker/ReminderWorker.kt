package com.mo.todo.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
        private const val TAG = "ReminderWorker"
        private const val PREFS_NAME = "reminder_notified"
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
            NotificationHelper.sendReminderNotification(applicationContext, todoId, todoTitle)
            markNotified(todoId)
            return Result.success()
        }

        // 定期扫描：只处理最近 30 分钟内过期且未通知的提醒
        val windowStart = now - 30 * 60 * 1000L
        val overdueTodos = database.todoDao().getUpcomingReminders(windowStart, now)
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var sent = 0
        overdueTodos.forEach { todo ->
            if (!prefs.getBoolean("notified_${todo.id}", false)) {
                NotificationHelper.sendReminderNotification(applicationContext, todo.id, todo.title)
                markNotified(todo.id)
                sent++
            }
        }
        if (sent > 0) Log.d(TAG, "Periodic scan sent $sent notifications")
        return Result.success()
    }

    private fun markNotified(todoId: Long) {
        applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean("notified_$todoId", true).apply()
    }
}
