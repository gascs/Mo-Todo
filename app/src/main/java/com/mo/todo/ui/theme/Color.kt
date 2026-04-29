package com.mo.todo.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════
//  Cloud Code Color System
//  Inspired by: GitHub Primer Design System
//  https://primer.style  |  MIT License
// ═══════════════════════════════════════════════

enum class ColorTheme(val key: String, val label: String) {
    SKYLINE("skyline", "天际蓝"),
    AZURE("azure", "云天蓝"),
    CYPRESS("cypress", "森青绿"),
    AMETHYST("amethyst", "暗夜紫"),
    GRAPHITE("graphite", "石墨灰");

    companion object {
        fun fromKey(key: String) = entries.firstOrNull { it.key == key } ?: SKYLINE
    }
}

enum class FontSize(val key: String, val label: String, val scale: Float) {
    SMALL("small", "小", 0.85f),
    MEDIUM("medium", "中", 1.0f),
    LARGE("large", "大", 1.15f);

    companion object {
        fun fromKey(key: String) = entries.firstOrNull { it.key == key } ?: MEDIUM
    }
}

enum class CornerStyle(val key: String, val label: String, val multiplier: Float) {
    SQUARE("square", "方角", 0f),
    ROUNDED("rounded", "圆角", 1f),
    EXTRA_ROUNDED("extra_rounded", "超圆角", 2f);

    companion object {
        fun fromKey(key: String) = entries.firstOrNull { it.key == key } ?: ROUNDED
    }
}

fun getThemeColors(theme: ColorTheme): Pair<Color, Color> = when (theme) {
    ColorTheme.SKYLINE -> Color(0xFF0969DA) to Color(0xFF539BF5)
    ColorTheme.AZURE -> Color(0xFF0078D4) to Color(0xFF4DA3E0)
    ColorTheme.CYPRESS -> Color(0xFF1A7F37) to Color(0xFF4AC26B)
    ColorTheme.AMETHYST -> Color(0xFF8250DF) to Color(0xFFA371F7)
    ColorTheme.GRAPHITE -> Color(0xFF656D76) to Color(0xFF8B949E)
}

// ── Light Mode Surface Colors (Primer light) ──
val MoLightBackground = Color(0xFFFFFFFF)
val MoLightSurface = Color(0xFFF6F8FA)
val MoLightOnSurface = Color(0xFF1F2328)
val MoLightOnSurfaceVariant = Color(0xFF656D76)
val MoLightOutline = Color(0xFFD0D7DE)
val MoLightOutlineVariant = Color(0xFFE8ECF0)

// ── Dark Mode Surface Colors (GitHub Dark) ──
val MoDarkBackground = Color(0xFF0D1117)
val MoDarkSurface = Color(0xFF161B22)
val MoDarkOnSurface = Color(0xFFE6EDF3)
val MoDarkOnSurfaceVariant = Color(0xFF8B949E)
val MoDarkOutline = Color(0xFF30363D)
val MoDarkOutlineVariant = Color(0xFF21262D)

// ── Error ──
val MoError = Color(0xFFCF222E)
val MoOnError = Color(0xFFFFFFFF)
val MoErrorContainer = Color(0xFFFFEBE9)
val MoOnErrorContainer = Color(0xFF7A1A1A)

val MoErrorDark = Color(0xFFFF7B72)
val MoOnErrorDark = Color(0xFF490202)
val MoErrorContainerDark = Color(0xFF7A1A1A)
val MoOnErrorContainerDark = Color(0xFFFFDAD6)

// ── Surface Variant ──
val MoSurfaceVariantLight = Color(0xFFE8ECF0)
val MoSurfaceVariantDark = Color(0xFF21262D)

// ── Functional Accents ──
val StarColor = Color(0xFFD4A72C)
val PriorityHigh = Color(0xFFCF222E)
val PriorityMedium = Color(0xFFBF8700)
val PriorityLow = Color(0xFF0969DA)

val MemoChipColors = listOf(
    Color(0xFFDDF4FF), Color(0xFFDAFBE1), Color(0xFFFFF8C5),
    Color(0xFFFBEFFF), Color(0xFFFFEBE9), Color(0xFFFFE2BD), Color(0xFFB6E3FF)
)

// ── Primary (Primer Blue) ──
val MoPrimary = Color(0xFF0969DA)
val MoOnPrimary = Color(0xFFFFFFFF)
val MoPrimaryContainer = Color(0xFFDDF4FF)
val MoOnPrimaryContainer = Color(0xFF04305B)

val MoPrimaryDark = Color(0xFF539BF5)
val MoOnPrimaryDark = Color(0xFF010D1C)
val MoPrimaryContainerDark = Color(0xFF04305B)
val MoOnPrimaryContainerDark = Color(0xFFB6E3FF)

// ── Secondary (Primer Gray) ──
val MoSecondary = Color(0xFF656D76)
val MoOnSecondary = Color(0xFFFFFFFF)
val MoSecondaryContainer = Color(0xFFF6F8FA)
val MoOnSecondaryContainer = Color(0xFF1F2328)

val MoSecondaryDark = Color(0xFF8B949E)
val MoOnSecondaryDark = Color(0xFF1F2328)
val MoSecondaryContainerDark = Color(0xFF30363D)
val MoOnSecondaryContainerDark = Color(0xFFD0D7DE)
