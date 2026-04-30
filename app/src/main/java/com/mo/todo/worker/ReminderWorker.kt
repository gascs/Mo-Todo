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
            return Result.success()
        }

        // 备用：扫描所有过期提醒
        val overdueTodos = database.todoDao().getUpcomingReminders(0, now)
        Log.d(TAG, "Overdue: ${overdueTodos.size}")
        overdueTodos.forEach {
            NotificationHelper.sendReminderNotification(applicationContext, it.id, it.title)
        }
        return Result.success()
    }
}
