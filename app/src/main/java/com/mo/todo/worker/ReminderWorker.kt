package com.mo.todo.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
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

        fun createChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "待办事项与备忘录提醒通知"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 200, 300)
                }
                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager.createNotificationChannel(channel)
                Log.d(TAG, "Notification channel created: $CHANNEL_ID")
            }
        }
    }

    override suspend fun doWork(): Result {
        val todoId = inputData.getLong("todo_id", -1L)
        val todoTitle = inputData.getString("todo_title")
        val now = System.currentTimeMillis()

        Log.d(TAG, "doWork() triggered at $now, todoId=$todoId, todoTitle=$todoTitle")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "POST_NOTIFICATIONS permission not granted, skipping")
                return Result.success()
            }
        }

        if (todoId > 0 && todoTitle != null) {
            Log.d(TAG, "Sending notification for id=$todoId, title=$todoTitle")
            sendNotification(todoId, todoTitle)
            return Result.success()
        }

        val overdueTodos = database.todoDao().getUpcomingReminders(0, now)
        Log.d(TAG, "Overdue todos count: ${overdueTodos.size}")

        overdueTodos.forEach { todo ->
            Log.d(TAG, "Sending notification for overdue todo: id=${todo.id}, title=${todo.title}, reminderTime=${todo.reminderTime}, diff=${now - (todo.reminderTime ?: 0)}ms")
            sendNotification(todo.id, todo.title)
        }

        return Result.success()
    }

    private fun sendNotification(todoId: Long, title: String) {
        try {
            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                todoId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText("待办事项提醒")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 300, 200, 300))
                .build()

            NotificationManagerCompat.from(applicationContext).notify(todoId.toInt(), notification)
            Log.i(TAG, "Notification sent successfully: id=$todoId, title=$title")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send notification: ${e.message}", e)
        }
    }
}
