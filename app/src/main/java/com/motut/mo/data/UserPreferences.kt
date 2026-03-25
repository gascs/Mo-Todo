package com.motut.mo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val KEY_DEFAULT_TODO_PRIORITY = stringPreferencesKey("default_todo_priority")
    }

    val darkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_DARK_MODE] ?: false
        }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] ?: true
        }

    val defaultTodoPriority: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_DEFAULT_TODO_PRIORITY] ?: Priority.MEDIUM.name
        }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DARK_MODE] = enabled
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
}
