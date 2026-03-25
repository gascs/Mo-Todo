package com.motut.mo.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.motut.mo.MainActivity
import com.motut.mo.R

class NotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_TODO_REMINDER = "todo_reminder"
        const val CHANNEL_GENERAL = "general"
        const val NOTIFICATION_ID_TODO_REMINDER = 1001
    }

    private val notificationManager = context.getSystemService(AndroidNotificationManager::class.java)

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val todoReminderChannel = NotificationChannel(
                CHANNEL_TODO_REMINDER,
                context.getString(R.string.todo_reminder_channel_name),
                AndroidNotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.todo_reminder_channel_description)
                enableVibration(true)
                enableLights(true)
            }

            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                context.getString(R.string.general_channel_name),
                AndroidNotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.general_channel_description)
            }

            notificationManager.createNotificationChannel(todoReminderChannel)
            notificationManager.createNotificationChannel(generalChannel)
        }
    }

    fun showTodoReminderNotification(
        todoId: Long,
        title: String,
        content: String? = null
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("todo_id", todoId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            todoId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_TODO_REMINDER)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content ?: context.getString(R.string.todo_reminder_default_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        notificationManager.notify(todoId.toInt(), notification)
    }

    fun showGeneralNotification(
        notificationId: Int,
        title: String,
        content: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}
