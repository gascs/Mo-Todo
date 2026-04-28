package com.mo.todo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green80,
    onPrimaryContainer = Color(0xFF0A1F10),
    secondary = GreenGray40,
    onSecondary = Color.White,
    secondaryContainer = GreenGray80,
    onSecondaryContainer = Color(0xFF18271B),
    tertiary = Teal40,
    onTertiary = Color.White,
    tertiaryContainer = Teal80,
    onTertiaryContainer = Color(0xFF00201C),
    surface = Color(0xFFFAFAF7),
    onSurface = Color(0xFF1A1C19),
    surfaceVariant = Color(0xFFDEE4DB),
    onSurfaceVariant = Color(0xFF43483F),
    outline = Color(0xFF73786F),
    outlineVariant = Color(0xFFC2C8BD),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Color(0xFF0A1F10),
    primaryContainer = Color(0xFF3A5A40),
    onPrimaryContainer = Green90,
    secondary = GreenGray80,
    onSecondary = Color(0xFF18271B),
    secondaryContainer = Color(0xFF435346),
    onSecondaryContainer = GreenGray90,
    tertiary = Teal80,
    onTertiary = Color(0xFF00201C),
    tertiaryContainer = Color(0xFF2A4F48),
    onTertiaryContainer = Color(0xFFD9F2EE),
    surface = Color(0xFF1A1C19),
    onSurface = Color(0xFFE2E3DF),
    surfaceVariant = Color(0xFF43483F),
    onSurfaceVariant = Color(0xFFC2C8BD),
    outline = Color(0xFF8C9288),
    outlineVariant = Color(0xFF43483F),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun MoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
