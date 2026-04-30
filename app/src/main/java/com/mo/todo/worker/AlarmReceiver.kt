package com.mo.todo.worker

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.mo.todo.data.database.AppDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var database: AppDatabase

    companion object {
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getLongExtra("todo_id", -1L)
        val todoTitle = intent.getStringExtra("todo_title") ?: return
        Log.d(TAG, "Alarm triggered: id=$todoId, title=$todoTitle")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "No notification permission")
            return
        }

        if (todoId > 0) {
            NotificationHelper.sendReminderNotification(context, todoId, todoTitle)
        } else {
            // 扫描所有过期提醒
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val now = System.currentTimeMillis()
                    val overdueTodos = database.todoDao().getUpcomingReminders(0, now)
                    Log.d(TAG, "Overdue reminders: ${overdueTodos.size}")
                    overdueTodos.forEach {
                        NotificationHelper.sendReminderNotification(context, it.id, it.title)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error scanning reminders: ${e.message}", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
