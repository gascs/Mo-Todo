package com.motut.mo

import android.app.Application
import com.motut.mo.data.AppDatabaseHelper
import com.motut.mo.data.DataBackupManager
import com.motut.mo.data.UserPreferences

class MoApplication : Application() {

    lateinit var databaseHelper: AppDatabaseHelper
        private set

    lateinit var userPreferences: UserPreferences
        private set

    lateinit var dataBackupManager: DataBackupManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        databaseHelper = AppDatabaseHelper.getInstance(this)
        userPreferences = UserPreferences(this)
        dataBackupManager = DataBackupManager(this)
    }

    companion object {
        lateinit var instance: MoApplication
            private set
    }
}
