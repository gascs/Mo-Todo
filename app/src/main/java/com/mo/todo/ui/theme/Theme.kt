package com.mo.todo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val MoLightScheme = lightColorScheme(
    primary = MoPrimary,
    onPrimary = MoOnPrimary,
    primaryContainer = MoPrimaryContainer,
    onPrimaryContainer = MoOnPrimaryContainer,
    secondary = MoSecondary,
    onSecondary = MoOnSecondary,
    secondaryContainer = MoSecondaryContainer,
    onSecondaryContainer = MoOnSecondaryContainer,
    error = MoError,
    onError = MoOnError,
    errorContainer = MoErrorContainer,
    onErrorContainer = MoOnErrorContainer,
    background = MoLightBackground,
    onBackground = MoLightOnSurface,
    surface = MoLightSurface,
    onSurface = MoLightOnSurface,
    surfaceVariant = MoSurfaceVariantLight,
    onSurfaceVariant = MoLightOnSurfaceVariant,
    outline = MoLightOutline,
    outlineVariant = MoLightOutlineVariant,
)

private val MoDarkScheme = darkColorScheme(
    primary = MoPrimaryDark,
    onPrimary = MoOnPrimaryDark,
    primaryContainer = MoPrimaryContainerDark,
    onPrimaryContainer = MoOnPrimaryContainerDark,
    secondary = MoSecondaryDark,
    onSecondary = MoOnSecondaryDark,
    secondaryContainer = MoSecondaryContainerDark,
    onSecondaryContainer = MoOnSecondaryContainerDark,
    error = MoErrorDark,
    onError = MoOnErrorDark,
    errorContainer = MoErrorContainerDark,
    onErrorContainer = MoOnErrorContainerDark,
    background = MoDarkBackground,
    onBackground = MoDarkOnSurface,
    surface = MoDarkSurface,
    onSurface = MoDarkOnSurface,
    surfaceVariant = MoSurfaceVariantDark,
    onSurfaceVariant = MoDarkOnSurfaceVariant,
    outline = MoDarkOutline,
    outlineVariant = MoDarkOutlineVariant,
)

val MoShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
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
        darkTheme -> MoDarkScheme
        else -> MoLightScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MoTypography,
        shapes = MoShapes,
        content = content
    )
}
