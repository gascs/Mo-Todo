package com.mo.todo.ui.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeMode(val value: Int) {
    SYSTEM(0),
    LIGHT(1),
    DARK(2);

    companion object {
        fun fromValue(value: Int): ThemeMode = entries.firstOrNull { it.value == value } ?: SYSTEM
    }
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private object PreferencesKeys {
        val THEME_MODE = intPreferencesKey("theme_mode")
        val IS_DYNAMIC_COLOR = booleanPreferencesKey("is_dynamic_color")
        val DEFAULT_REMINDER_ENABLED = booleanPreferencesKey("default_reminder_enabled")
        val DEFAULT_REMINDER_MINUTES = intPreferencesKey("default_reminder_minutes")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        ThemeMode.fromValue(preferences[PreferencesKeys.THEME_MODE] ?: 0)
    }

    val isDynamicColor: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_DYNAMIC_COLOR] ?: true
    }

    val defaultReminderEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_REMINDER_ENABLED] ?: false
    }

    val defaultReminderMinutes: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_REMINDER_MINUTES] ?: 10
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.value
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun setDefaultReminderMinutes(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_REMINDER_MINUTES] = minutes
        }
    }
}
