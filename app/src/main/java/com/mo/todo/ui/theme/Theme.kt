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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

fun buildMoShapes(cornerMultiplier: Float = 1f): Shapes = Shapes(
    extraSmall = RoundedCornerShape((6 * cornerMultiplier).dp),
    small = RoundedCornerShape((10 * cornerMultiplier).dp),
    medium = RoundedCornerShape((14 * cornerMultiplier).dp),
    large = RoundedCornerShape((18 * cornerMultiplier).dp),
    extraLarge = RoundedCornerShape((24 * cornerMultiplier).dp),
)

val MoShapes = buildMoShapes()

fun buildLightScheme(primary: androidx.compose.ui.graphics.Color, onPrimary: androidx.compose.ui.graphics.Color, primaryContainer: androidx.compose.ui.graphics.Color, onPrimaryContainer: androidx.compose.ui.graphics.Color) = lightColorScheme(
    primary = primary, onPrimary = onPrimary, primaryContainer = primaryContainer, onPrimaryContainer = onPrimaryContainer,
    secondary = MoSecondary, onSecondary = MoOnSecondary, secondaryContainer = MoSecondaryContainer, onSecondaryContainer = MoOnSecondaryContainer,
    error = MoError, onError = MoOnError, errorContainer = MoErrorContainer, onErrorContainer = MoOnErrorContainer,
    background = MoLightBackground, onBackground = MoLightOnSurface,
    surface = MoLightSurface, onSurface = MoLightOnSurface,
    surfaceVariant = MoSurfaceVariantLight, onSurfaceVariant = MoLightOnSurfaceVariant,
    outline = MoLightOutline, outlineVariant = MoLightOutlineVariant,
)

fun buildDarkScheme(primary: androidx.compose.ui.graphics.Color, onPrimary: androidx.compose.ui.graphics.Color, primaryContainer: androidx.compose.ui.graphics.Color, onPrimaryContainer: androidx.compose.ui.graphics.Color) = darkColorScheme(
    primary = primary, onPrimary = onPrimary, primaryContainer = primaryContainer, onPrimaryContainer = onPrimaryContainer,
    secondary = MoSecondaryDark, onSecondary = MoOnSecondaryDark, secondaryContainer = MoSecondaryContainerDark, onSecondaryContainer = MoOnSecondaryContainerDark,
    error = MoErrorDark, onError = MoOnErrorDark, errorContainer = MoErrorContainerDark, onErrorContainer = MoOnErrorContainerDark,
    background = MoDarkBackground, onBackground = MoDarkOnSurface,
    surface = MoDarkSurface, onSurface = MoDarkOnSurface,
    surfaceVariant = MoSurfaceVariantDark, onSurfaceVariant = MoDarkOnSurfaceVariant,
    outline = MoDarkOutline, outlineVariant = MoDarkOutlineVariant,
)

@Composable
fun MoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    colorTheme: ColorTheme = ColorTheme.SKYLINE,
    fontScale: Float = 1.0f,
    cornerMultiplier: Float = 1.0f,
    content: @Composable () -> Unit
) {
    val (primaryLight, primaryDark) = getThemeColors(colorTheme)
    val lightScheme = buildLightScheme(primaryLight, MoOnPrimary, MoPrimaryContainer, MoOnPrimaryContainer)
    val darkScheme = buildDarkScheme(primaryDark, MoOnPrimaryDark, MoPrimaryContainerDark, MoOnPrimaryContainerDark)

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkScheme
        else -> lightScheme
    }

    val typography = buildTypography(fontScale)
    val shapes = buildMoShapes(cornerMultiplier)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
