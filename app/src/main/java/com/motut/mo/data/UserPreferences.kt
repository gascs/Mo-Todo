package com.motut.mo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val KEY_NOTIFICATION_REMINDER_TIME = intPreferencesKey("notification_reminder_time")
        private val KEY_DEFAULT_TODO_PRIORITY = stringPreferencesKey("default_todo_priority")
        private val KEY_BACKGROUND_IMAGE_URI = stringPreferencesKey("background_image_uri")
        private val KEY_CUSTOM_PRIMARY_COLOR = intPreferencesKey("custom_primary_color")
        private val KEY_USE_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
        private val KEY_APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        private val KEY_CUSTOM_SYNC_SOURCE = stringPreferencesKey("custom_sync_source")
        private val KEY_SHOW_ANNOUNCEMENT = booleanPreferencesKey("show_announcement")
    }

    val themeMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_THEME_MODE] ?: ThemeMode.SYSTEM.name
        }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] ?: true
        }

    val defaultTodoPriority: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_DEFAULT_TODO_PRIORITY] ?: Priority.MEDIUM.name
        }

    val backgroundImageUri: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_BACKGROUND_IMAGE_URI] ?: ""
        }

    val customPrimaryColor: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_CUSTOM_PRIMARY_COLOR] ?: 0
        }

    val useDynamicColor: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_USE_DYNAMIC_COLOR] ?: true
        }

    val appLockEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_APP_LOCK_ENABLED] ?: false
        }

    val notificationReminderTime: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_NOTIFICATION_REMINDER_TIME] ?: 10
        }

    val customSyncSource: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_CUSTOM_SYNC_SOURCE] ?: ""
        }

    val showAnnouncement: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_SHOW_ANNOUNCEMENT] ?: true
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode.name
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setDefaultTodoPriority(priority: Priority) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DEFAULT_TODO_PRIORITY] = priority.name
        }
    }

    suspend fun setBackgroundImageUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BACKGROUND_IMAGE_URI] = uri
        }
    }

    suspend fun setCustomPrimaryColor(color: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CUSTOM_PRIMARY_COLOR] = color
        }
    }

    suspend fun setUseDynamicColor(use: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USE_DYNAMIC_COLOR] = use
        }
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_APP_LOCK_ENABLED] = enabled
        }
    }

    suspend fun setNotificationReminderTime(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATION_REMINDER_TIME] = minutes
        }
    }

    suspend fun setCustomSyncSource(source: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CUSTOM_SYNC_SOURCE] = source
        }
    }

    suspend fun setShowAnnouncement(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SHOW_ANNOUNCEMENT] = show
        }
    }
}

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}
