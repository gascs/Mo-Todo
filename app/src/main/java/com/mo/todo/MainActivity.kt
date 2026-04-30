package com.mo.todo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.ui.screen.MainScreen
import com.mo.todo.ui.theme.ColorTheme
import com.mo.todo.ui.theme.CornerStyle
import com.mo.todo.ui.theme.FontSize
import com.mo.todo.ui.theme.MoTheme
import com.mo.todo.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var pendingTodoId by mutableLongStateOf(-1L)

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        Log.d("MainActivity", "POST_NOTIFICATIONS permission: $granted")
        if (granted) requestBatteryOptimization()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                requestBatteryOptimization()
            }
        } else {
            requestBatteryOptimization()
        }

        pendingTodoId = intent.getLongExtra("todo_id", -1L)

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()
            val colorThemeKey by settingsViewModel.colorTheme.collectAsState()
            val fontSizeKey by settingsViewModel.fontSize.collectAsState()
            val cornerStyleKey by settingsViewModel.cornerStyle.collectAsState()
            val isDynamicColor by settingsViewModel.isDynamicColor.collectAsState()
            val language by settingsViewModel.language.collectAsState()

            LaunchedEffect(language) {
                val locales = when (language) {
                    "zh" -> LocaleListCompat.forLanguageTags("zh")
                    "en" -> LocaleListCompat.forLanguageTags("en")
                    else -> LocaleListCompat.getEmptyLocaleList()
                }
                AppCompatDelegate.setApplicationLocales(locales)
            }

            val colorTheme = ColorTheme.fromKey(colorThemeKey)
            val fontScale = FontSize.fromKey(fontSizeKey).scale
            val cornerMult = CornerStyle.fromKey(cornerStyleKey).multiplier

            MoTheme(
                darkTheme = isDarkTheme,
                dynamicColor = isDynamicColor,
                colorTheme = colorTheme,
                fontScale = fontScale,
                cornerMultiplier = cornerMult
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(
                        pendingTodoId = pendingTodoId,
                        onTodoIdConsumed = { pendingTodoId = -1L }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingTodoId = intent.getLongExtra("todo_id", -1L)
    }

    private fun requestBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                try {
                    @Suppress("Deprecation")
                    startActivity(android.content.Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = android.net.Uri.parse("package:$packageName")
                    })
                } catch (_: Exception) {
                    Log.w("MainActivity", "Failed to open battery optimization dialog")
                }
            }
        }
    }
}
