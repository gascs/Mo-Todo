package com.mo.todo.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════
//  Cloud Code Color System
//  Inspired by: VS Code / GitHub / AWS Console
// ═══════════════════════════════════════════════

enum class ColorTheme(val key: String, val label: String) {
    SKYLINE("skyline", "天际蓝"),
    AZURE("azure", "云端蓝"),
    CYPRESS("cypress", "终端青"),
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
    ColorTheme.SKYLINE -> Color(0xFF3B82F6) to Color(0xFF60A5FA)
    ColorTheme.AZURE -> Color(0xFF0078D4) to Color(0xFF4DA3E0)
    ColorTheme.CYPRESS -> Color(0xFF0D9488) to Color(0xFF2DD4BF)
    ColorTheme.AMETHYST -> Color(0xFF7C3AED) to Color(0xFFA78BFA)
    ColorTheme.GRAPHITE -> Color(0xFF475569) to Color(0xFF94A3B8)
}

// ── Light Mode Surface Colors ──
val MoLightBackground = Color(0xFFF8FAFC)
val MoLightSurface = Color(0xFFF1F5F9)
val MoLightOnSurface = Color(0xFF0F172A)
val MoLightOnSurfaceVariant = Color(0xFF64748B)
val MoLightOutline = Color(0xFFE2E8F0)
val MoLightOutlineVariant = Color(0xFFF1F5F9)

// ── Dark Mode Surface Colors (VS Code-inspired) ──
val MoDarkBackground = Color(0xFF1E1E24)
val MoDarkSurface = Color(0xFF282831)
val MoDarkOnSurface = Color(0xFFE2E8F0)
val MoDarkOnSurfaceVariant = Color(0xFF94A3B8)
val MoDarkOutline = Color(0xFF3E3E48)
val MoDarkOutlineVariant = Color(0xFF33333D)

// ── Error ──
val MoError = Color(0xFFDC2626)
val MoOnError = Color(0xFFFFFFFF)
val MoErrorContainer = Color(0xFFFEF2F2)
val MoOnErrorContainer = Color(0xFF7F1D1D)

val MoErrorDark = Color(0xFFF87171)
val MoOnErrorDark = Color(0xFF450A0A)
val MoErrorContainerDark = Color(0xFF7F1D1D)
val MoOnErrorContainerDark = Color(0xFFFEE2E2)

// ── Surface Variant ──
val MoSurfaceVariantLight = Color(0xFFEDF0F4)
val MoSurfaceVariantDark = Color(0xFF2E3038)

// ── Functional Accents ──
val StarColor = Color(0xFFEAB308)
val PriorityHigh = Color(0xFFEF4444)
val PriorityMedium = Color(0xFFF59E0B)
val PriorityLow = Color(0xFF3B82F6)

val MemoChipColors = listOf(
    Color(0xFF93C5FD), Color(0xFF86EFAC), Color(0xFF67E8F9),
    Color(0xFFC4B5FD), Color(0xFFFDA4AF), Color(0xFFFDE68A), Color(0xFFD4D4D8)
)

// ── Primary (Skyline Blue, default) ──
val MoPrimary = Color(0xFF3B82F6)
val MoOnPrimary = Color(0xFFFFFFFF)
val MoPrimaryContainer = Color(0xFFDBEAFE)
val MoOnPrimaryContainer = Color(0xFF1E3A5F)

val MoPrimaryDark = Color(0xFF60A5FA)
val MoOnPrimaryDark = Color(0xFF0F1B2D)
val MoPrimaryContainerDark = Color(0xFF1E3A5F)
val MoOnPrimaryContainerDark = Color(0xFFBFD9FE)

// ── Secondary (Slate) ──
val MoSecondary = Color(0xFF64748B)
val MoOnSecondary = Color(0xFFFFFFFF)
val MoSecondaryContainer = Color(0xFFF1F5F9)
val MoOnSecondaryContainer = Color(0xFF1E293B)

val MoSecondaryDark = Color(0xFF94A3B8)
val MoOnSecondaryDark = Color(0xFF1E293B)
val MoSecondaryContainerDark = Color(0xFF334155)
val MoOnSecondaryContainerDark = Color(0xFFCBD5E1)
