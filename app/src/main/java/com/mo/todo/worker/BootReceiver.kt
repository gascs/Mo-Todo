package com.mo.todo.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mo.todo.data.database.AppDatabase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var database: AppDatabase

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            ReminderWorker.rescheduleAllAfterBoot(context, database)
        }
    }
}
