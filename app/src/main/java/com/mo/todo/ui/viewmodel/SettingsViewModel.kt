package com.mo.todo.ui.viewmodel

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeMode(val value: Int) { SYSTEM(0), LIGHT(1), DARK(2);
    companion object { fun fromValue(v: Int) = entries.firstOrNull { it.value == v } ?: SYSTEM }
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private object K {
        val THEME_MODE = intPreferencesKey("theme_mode")
        val COLOR_THEME = stringPreferencesKey("color_theme")
        val FONT_SIZE = stringPreferencesKey("font_size")
        val CORNER_STYLE = stringPreferencesKey("corner_style")
        val DEFAULT_REMINDER_MINUTES = intPreferencesKey("default_reminder_minutes")
        val CUSTOM_LABELS = stringSetPreferencesKey("custom_labels")
        val NICKNAME = stringPreferencesKey("nickname")
        val AVATAR_PATH = stringPreferencesKey("avatar_path")
        val WEBDAC_URL = stringPreferencesKey("webdav_url")
        val WEBDAC_USER = stringPreferencesKey("webdav_user")
        val IS_DYNAMIC_COLOR = booleanPreferencesKey("is_dynamic_color")
    }

    private fun isSystemInDarkTheme(): Boolean =
        (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { ThemeMode.fromValue(it[K.THEME_MODE] ?: 0) }

    val isDarkTheme: StateFlow<Boolean> = themeMode.map { mode ->
        when (mode) { ThemeMode.SYSTEM -> isSystemInDarkTheme(); ThemeMode.LIGHT -> false; ThemeMode.DARK -> true }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, isSystemInDarkTheme())

    val colorTheme: StateFlow<String> = context.dataStore.data.map { it[K.COLOR_THEME] ?: "forest" }.stateIn(viewModelScope, SharingStarted.Eagerly, "forest")
    val fontSize: StateFlow<String> = context.dataStore.data.map { it[K.FONT_SIZE] ?: "medium" }.stateIn(viewModelScope, SharingStarted.Eagerly, "medium")
    val cornerStyle: StateFlow<String> = context.dataStore.data.map { it[K.CORNER_STYLE] ?: "rounded" }.stateIn(viewModelScope, SharingStarted.Eagerly, "rounded")
    val defaultReminderMinutesState: StateFlow<Int> = context.dataStore.data.map { it[K.DEFAULT_REMINDER_MINUTES] ?: 10 }.stateIn(viewModelScope, SharingStarted.Eagerly, 10)
    val nickname: StateFlow<String> = context.dataStore.data.map { it[K.NICKNAME] ?: "Mo 用户" }.stateIn(viewModelScope, SharingStarted.Eagerly, "Mo 用户")
    val avatarPath: StateFlow<String> = context.dataStore.data.map { it[K.AVATAR_PATH] ?: "" }.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val isDynamicColor: StateFlow<Boolean> = context.dataStore.data.map { it[K.IS_DYNAMIC_COLOR] ?: false }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val customLabels: Flow<Set<String>> = context.dataStore.data.map { it[K.CUSTOM_LABELS] ?: emptySet() }
    private val _customLabelsState = MutableStateFlow<Set<String>>(emptySet())
    val customLabelsState: StateFlow<Set<String>> = _customLabelsState.asStateFlow()
    init { viewModelScope.launch { customLabels.collect { _customLabelsState.value = it } } }

    suspend fun setThemeMode(mode: ThemeMode) { context.dataStore.edit { it[K.THEME_MODE] = mode.value } }
    suspend fun setColorTheme(key: String) { context.dataStore.edit { it[K.COLOR_THEME] = key } }
    suspend fun setFontSize(key: String) { context.dataStore.edit { it[K.FONT_SIZE] = key } }
    suspend fun setCornerStyle(key: String) { context.dataStore.edit { it[K.CORNER_STYLE] = key } }
    suspend fun setDefaultReminderMinutes(minutes: Int) { context.dataStore.edit { it[K.DEFAULT_REMINDER_MINUTES] = minutes } }
    suspend fun setNickname(name: String) { context.dataStore.edit { it[K.NICKNAME] = name } }
    suspend fun setAvatarPath(path: String) { context.dataStore.edit { it[K.AVATAR_PATH] = path } }
    suspend fun setDynamicColor(enabled: Boolean) { context.dataStore.edit { it[K.IS_DYNAMIC_COLOR] = enabled } }

    suspend fun addCustomLabel(label: String) { context.dataStore.edit { val cur = it[K.CUSTOM_LABELS] ?: emptySet(); it[K.CUSTOM_LABELS] = cur + label } }
    suspend fun removeCustomLabel(label: String) { context.dataStore.edit { val cur = it[K.CUSTOM_LABELS] ?: emptySet(); it[K.CUSTOM_LABELS] = cur - label } }
    suspend fun removeCustomLabels(labels: Set<String>) { context.dataStore.edit { val cur = it[K.CUSTOM_LABELS] ?: emptySet(); it[K.CUSTOM_LABELS] = cur - labels } }
    suspend fun renameCustomLabel(oldName: String, newName: String) { context.dataStore.edit { val cur = it[K.CUSTOM_LABELS] ?: emptySet(); it[K.CUSTOM_LABELS] = cur - oldName + newName } }

    suspend fun saveWebDavConfig(url: String, user: String, pass: String) {
        context.dataStore.edit { it[K.WEBDAC_URL] = url; it[K.WEBDAC_USER] = user }
        saveEncryptedPassword(pass)
    }

    suspend fun getWebDavConfig(): Triple<String, String, String>? {
        val prefs = context.dataStore.data.first()
        val url = prefs[K.WEBDAC_URL] ?: ""; val user = prefs[K.WEBDAC_USER] ?: ""
        val pass = getEncryptedPassword()
        return if (url.isNotBlank()) Triple(url, user, pass ?: "") else null
    }

    fun clearWebDavConfig() {
        viewModelScope.launch {
            context.dataStore.edit { it.remove(K.WEBDAC_URL); it.remove(K.WEBDAC_USER) }
            clearEncryptedPassword()
        }
    }

    private fun saveEncryptedPassword(password: String) {
        try {
            val prefs = context.getSharedPreferences("mo_secure", Context.MODE_PRIVATE)
            prefs.edit().putString("webdav_pass", password).apply()
        } catch (_: Exception) {}
    }

    private fun getEncryptedPassword(): String? {
        return try {
            context.getSharedPreferences("mo_secure", Context.MODE_PRIVATE).getString("webdav_pass", null)
        } catch (_: Exception) { null }
    }

    private fun clearEncryptedPassword() {
        try {
            context.getSharedPreferences("mo_secure", Context.MODE_PRIVATE).edit().remove("webdav_pass").apply()
        } catch (_: Exception) {}
    }
}
