package com.mo.todo.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════
//  Morandi Color System
//  Low-saturation primary + YumeBox lively accents
// ═══════════════════════════════════════════════

enum class ColorTheme(val key: String, val label: String) {
    FOREST("forest", "森林绿"),
    OCEAN("ocean", "深海蓝"),
    SUNSET("sunset", "晚霞橙"),
    LAVENDER("lavender", "薰衣草紫"),
    ROSEGOLD("rosegold", "玫瑰金");

    companion object {
        fun fromKey(key: String) = entries.firstOrNull { it.key == key } ?: FOREST
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
    ColorTheme.FOREST -> Color(0xFF5B7F6A) to Color(0xFF8BB59A)
    ColorTheme.OCEAN -> Color(0xFF3A7CA5) to Color(0xFF6EA8CF)
    ColorTheme.SUNSET -> Color(0xFFE0724B) to Color(0xFFF0A080)
    ColorTheme.LAVENDER -> Color(0xFF7B6B9E) to Color(0xFFB0A0C8)
    ColorTheme.ROSEGOLD -> Color(0xFFC0766A) to Color(0xFFE0A898)
}

// ── Light Mode Surface Colors ──
val MoLightBackground = Color(0xFFFAF8F5)
val MoLightSurface = Color(0xFFF2EFEA)
val MoLightOnSurface = Color(0xFF1C1B1A)
val MoLightOnSurfaceVariant = Color(0xFF6B6560)
val MoLightOutline = Color(0xFFD6D0C8)
val MoLightOutlineVariant = Color(0xFFE8E2DA)

// ── Dark Mode Surface Colors ──
val MoDarkBackground = Color(0xFF1B1A18)
val MoDarkSurface = Color(0xFF242320)
val MoDarkOnSurface = Color(0xFFE8E4DD)
val MoDarkOnSurfaceVariant = Color(0xFFA09A92)
val MoDarkOutline = Color(0xFF3A3733)
val MoDarkOutlineVariant = Color(0xFF2E2C28)

// ── Error ──
val MoError = Color(0xFFD9534F)
val MoOnError = Color(0xFFFFFFFF)
val MoErrorContainer = Color(0xFFFFEBEA)
val MoOnErrorContainer = Color(0xFF5A100E)

val MoErrorDark = Color(0xFFFF8A84)
val MoOnErrorDark = Color(0xFF3B0706)
val MoErrorContainerDark = Color(0xFF7A1A17)
val MoOnErrorContainerDark = Color(0xFFFFDAD6)

// ── Surface Variant ──
val MoSurfaceVariantLight = Color(0xFFF2EFEA)
val MoSurfaceVariantDark = Color(0xFF292826)

// ── Functional Accents ──
val StarColor = Color(0xFFEAB308)
val PriorityHigh = Color(0xFFE5605A)
val PriorityMedium = Color(0xFFE8A840)
val PriorityLow = Color(0xFF5B7F6A)

val MemoChipColors = listOf(
    Color(0xFFA8C9B4), Color(0xFFFFCDA8), Color(0xFFA3C5E8),
    Color(0xFFF0A8B6), Color(0xFFC9B8D8), Color(0xFFF5E6A3), Color(0xFFD4CDC3)
)

// ── Primary (Forest Green, default) ──
val MoPrimary = Color(0xFF5B7F6A)
val MoOnPrimary = Color(0xFFFFFFFF)
val MoPrimaryContainer = Color(0xFFDFEFE5)
val MoOnPrimaryContainer = Color(0xFF16291E)

val MoPrimaryDark = Color(0xFF8BB59A)
val MoOnPrimaryDark = Color(0xFF0B1F13)
val MoPrimaryContainerDark = Color(0xFF2A3F30)
val MoOnPrimaryContainerDark = Color(0xFFC7E8D2)

// ── Secondary ──
val MoSecondary = Color(0xFF6A7B6E)
val MoOnSecondary = Color(0xFFFFFFFF)
val MoSecondaryContainer = Color(0xFFECF5EE)
val MoOnSecondaryContainer = Color(0xFF16291E)

val MoSecondaryDark = Color(0xFFB9CCBD)
val MoOnSecondaryDark = Color(0xFF202E23)
val MoSecondaryContainerDark = Color(0xFF37443B)
val MoOnSecondaryContainerDark = Color(0xFFD2E7D5)

// ── Tertiary (YumeBox warm coral accent) ──
val MoTertiary = Color(0xFFE88A6E)
val MoOnTertiary = Color(0xFFFFFFFF)
val MoTertiaryContainer = Color(0xFFFFEDE6)
val MoOnTertiaryContainer = Color(0xFF5A1A0A)

val MoTertiaryDark = Color(0xFFFFB59E)
val MoOnTertiaryDark = Color(0xFF3D0A00)
val MoTertiaryContainerDark = Color(0xFF6E2B18)
val MoOnTertiaryContainerDark = Color(0xFFFFDBCF)
