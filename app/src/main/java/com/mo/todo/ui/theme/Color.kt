package com.mo.todo.ui.theme

import androidx.compose.ui.graphics.Color

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

val MoLightBackground = Color(0xFFFFFFFF)
val MoLightSurface = Color(0xFFF8F8F6)
val MoLightOnSurface = Color(0xFF1A1A1A)
val MoLightOnSurfaceVariant = Color(0xFF6B6B6B)
val MoLightOutline = Color(0xFFD6D6D6)
val MoLightOutlineVariant = Color(0xFFEAEAEA)

val MoDarkBackground = Color(0xFF1A1A1A)
val MoDarkSurface = Color(0xFF252525)
val MoDarkOnSurface = Color(0xFFE8E8E8)
val MoDarkOnSurfaceVariant = Color(0xFFA0A0A0)
val MoDarkOutline = Color(0xFF3A3A3A)
val MoDarkOutlineVariant = Color(0xFF2E2E2E)

val MoError = Color(0xFFD9534F)
val MoOnError = Color(0xFFFFFFFF)
val MoErrorContainer = Color(0xFFFFEBEA)
val MoOnErrorContainer = Color(0xFF5A100E)

val MoErrorDark = Color(0xFFFF8A84)
val MoOnErrorDark = Color(0xFF3B0706)
val MoErrorContainerDark = Color(0xFF7A1A17)
val MoOnErrorContainerDark = Color(0xFFFFDAD6)

val MoSurfaceVariantLight = Color(0xFFF2F2F0)
val MoSurfaceVariantDark = Color(0xFF292929)

val StarColor = Color(0xFFF0C040)

val PriorityHigh = Color(0xFFE5605A)
val PriorityMedium = Color(0xFFE8A840)
val PriorityLow = Color(0xFF5B7F6A)

val MemoChipColors = listOf(
    Color(0xFFA8C9B4), Color(0xFFFFCDA8), Color(0xFFA3C5E8),
    Color(0xFFF0A8B6), Color(0xFFC9B8D8), Color(0xFFF5E6A3), Color(0xFFD4CDC3)
)

val MoPrimary = Color(0xFF5B7F6A)
val MoOnPrimary = Color(0xFFFFFFFF)
val MoPrimaryContainer = Color(0xFFDFEFE5)
val MoOnPrimaryContainer = Color(0xFF16291E)

val MoPrimaryDark = Color(0xFF8BB59A)
val MoOnPrimaryDark = Color(0xFF0B1F13)
val MoPrimaryContainerDark = Color(0xFF2A3F30)
val MoOnPrimaryContainerDark = Color(0xFFC7E8D2)

val MoSecondary = Color(0xFF6A7B6E)
val MoOnSecondary = Color(0xFFFFFFFF)
val MoSecondaryContainer = Color(0xFFECF5EE)
val MoOnSecondaryContainer = Color(0xFF16291E)

val MoSecondaryDark = Color(0xFFB9CCBD)
val MoOnSecondaryDark = Color(0xFF202E23)
val MoSecondaryContainerDark = Color(0xFF37443B)
val MoOnSecondaryContainerDark = Color(0xFFD2E7D5)
