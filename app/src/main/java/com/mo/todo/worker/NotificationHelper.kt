package com.mo.todo.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mo.todo.MainActivity

object NotificationHelper {

    private const val TAG = "NotificationHelper"

    // 主提醒渠道 - 高优先级 heads-up
    const val CHANNEL_REMINDER = "mo_reminder"
    // 备用渠道 - 普通优先级
    const val CHANNEL_REMINDER_LOW = "mo_reminder_low"

    /**
     * 创建所有通知渠道（Application.onCreate 时调用）
     */
    fun createAllChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(NotificationManager::class.java)
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            // 主渠道 - IMPORTANCE_HIGH 触发 heads-up 横幅通知
            val mainChannel = NotificationChannel(CHANNEL_REMINDER, "待办提醒", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "待办事项到期提醒通知"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setSound(soundUri, audioAttrs)
                setShowBadge(true)
                enableLights(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }
            nm.createNotificationChannel(mainChannel)

            // 低优先级渠道（静默）
            val lowChannel = NotificationChannel(CHANNEL_REMINDER_LOW, "待办提醒（静默）", NotificationManager.IMPORTANCE_LOW).apply {
                description = "待办事项提醒（无声音振动）"
                setShowBadge(true)
                enableLights(true)
            }
            nm.createNotificationChannel(lowChannel)

            Log.d(TAG, "Notification channels created")
        }
    }

    /**
     * 发送提醒通知 - 使用 heads-up 模式
     */
    fun sendReminderNotification(context: Context, todoId: Long, title: String) {
        try {
            val vibrate = context.getSharedPreferences("mo_prefs", Context.MODE_PRIVATE)
                .getBoolean("notification_vibrate", true)

            // 点击通知打开 MainActivity
            val contentIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("todo_id", todoId)
            }
            val contentPendingIntent = PendingIntent.getActivity(
                context, todoId.toInt(), contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 全屏 Intent - 锁屏时也能弹出
            val fullScreenIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("todo_id", todoId)
            }
            val fullScreenPendingIntent = PendingIntent.getActivity(
                context, todoId.toInt() + 100000, fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val builder = NotificationCompat.Builder(context, CHANNEL_REMINDER)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("待办提醒")
                .setContentText(title)
                .setSubText("Mo Todo")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(contentPendingIntent)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)

            if (vibrate) {
                builder.setVibrate(longArrayOf(0, 300, 200, 300))
            }

            // 设置大文本样式，显示更多内容
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(title))

            NotificationManagerCompat.from(context).notify(todoId.toInt(), builder.build())
            Log.i(TAG, "Notification sent: id=$todoId, title=$title")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send notification: ${e.message}", e)
        }
    }
}
