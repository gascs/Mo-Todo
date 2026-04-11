package com.motut.mo.ui.theme

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

/**
 * 完整的亮色主题配色方案 - v2.0 现代优雅风格
 * - 更高的对比度
 * - 更清晰的层次
 * - 更好的可访问性
 */
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4F46E5),           // Indigo 600 - 更有活力
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E7FF),  // Indigo 100
    onPrimaryContainer = Color(0xFF1E1B4B),
    
    secondary = Color(0xFF7C3AED),         // Violet 600 - 辅助强调
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E8FF), // Violet 100
    onSecondaryContainer = Color(0xFF2E1065),
    
    tertiary = Color(0xFFF97316),           // Orange 500 - 温暖点缀
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFEDD5), // Orange 100
    onTertiaryContainer = Color(0xFF451A03),
    
    error = Color(0xFFEF4444),              // Red 500 - 更鲜明
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),     // Red 100
    onErrorContainer = Color(0xFF450A0A),
    
    background = Color(0xFFF8FAFC),        // Slate 50 - 更清爽
    onBackground = Color(0xFF1E293B),
    
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFFF1F5F9),     // Slate 100
    onSurfaceVariant = Color(0xFF64748B),
    
    outline = Color(0xFFCBD5E1),            // Slate 300
    outlineVariant = Color(0xFFE2E8F0),     // Slate 200
    
    // SurfaceTint: Material You 风格的高光强调
    surfaceTint = Color(0xFF4F46E5),
    
    // 额外的表面颜色
    inverseSurface = Color(0xFF1E293B),
    inverseOnSurface = Color(0xFFF8FAFC),
    inversePrimary = Color(0xFF818CF8)
)

/**
 * 完整的暗色主题配色方案 - v2.0 沉浸式深色
 * - 更温暖的色调
 * - 更好的对比度
 * - 更清晰的层次
 */
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),            // Indigo 400 - 更清晰
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = Color(0xFF3730A3),   // Indigo 800
    onPrimaryContainer = Color(0xFFE0E7FF),
    
    secondary = Color(0xFFC4B5FD),          // Violet 300
    onSecondary = Color(0xFF2E1065),
    secondaryContainer = Color(0xFF5B21B6),  // Violet 900
    onSecondaryContainer = Color(0xFFF3E8FF),
    
    tertiary = Color(0xFFFBBF24),           // Amber 400
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = Color(0xFFD97706),   // Amber 600
    onTertiaryContainer = Color(0xFFFEF3C7),
    
    error = Color(0xFFF87171),              // Red 400
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFFB91C1C),      // Red 700
    onErrorContainer = Color(0xFFFEE2E2),
    
    background = Color(0xFF0F172A),          // Slate 900 - 深邃背景
    onBackground = Color(0xFFE2E8F0),
    
    surface = Color(0xFF1E293B),            // Slate 800 - 提升层次
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF334155),     // Slate 700
    onSurfaceVariant = Color(0xFF94A3B8),
    
    outline = Color(0xFF475569),             // Slate 600
    outlineVariant = Color(0xFF334155),     // Slate 700
    
    // SurfaceTint: Material You 风格的高光强调
    surfaceTint = Color(0xFF818CF8),
    
    // 额外的表面颜色
    inverseSurface = Color(0xFFE2E8F0),
    inverseOnSurface = Color(0xFF0F172A),
    inversePrimary = Color(0xFF4F46E5)
)

/**
 * MoTodo 应用主题
 *
 * @param darkTheme 是否使用暗色主题
 * @param dynamicColor 是否使用动态颜色 (Material You，Android 12+)
 * @param customPrimaryColor 自定义主色，0 表示不使用
 * @param content 主题内容
 */
@Composable
fun MoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    customPrimaryColor: Int = 0,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        customPrimaryColor != 0 -> {
            val primary = Color(customPrimaryColor)
            val tonalPalette = if (darkTheme) {
                // 生成暗色变体
                darkColorScheme(primary = primary)
            } else {
                lightColorScheme(primary = primary)
            }
            tonalPalette
        }
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // 同步系统 UI 状态栏颜色
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // 设置状态栏颜色为透明，实现边缘到边缘效果
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}