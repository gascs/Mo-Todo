package com.mo.todo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

fun buildTypography(fontScale: Float = 1.0f): Typography = Typography(
    displayLarge = TextStyle(fontFamily = AppDefaultFont, fontWeight = FontWeight.Bold, fontSize = (24 * fontScale).sp),
    headlineLarge = TextStyle(fontFamily = AppDefaultFont, fontWeight = FontWeight.Bold, fontSize = (20 * fontScale).sp),
    headlineMedium = TextStyle(fontFamily = AppDefaultFont, fontWeight = FontWeight.SemiBold, fontSize = (18 * fontScale).sp),
    titleLarge = TextStyle(fontFamily = AppDefaultFont, fontWeight = FontWeight.SemiBold, fontSize = (16 * fontScale).sp, lineHeight = (24 * fontScale).sp),
    titleMedium = TextStyle(fontFamily = AppDefaultFont, fontWeight = FontWeight.Medium, fontSize = (15 * fontScale).sp, lineHeight = (22 * fontScale).sp),
    titleSmall = TextStyle(fontFamily = AppDefaultFont, fontWeight = FontWeight.Medium, fontSize = (14 * fontScale).sp, lineHeight = (20 * fontScale).sp),
    bodyLarge = TextStyle(fontFamily = AppDefaultFont, fontWeight = FontWeight.Normal, fontSize = (16 * fontScale).sp, lineHeight = (24 * fontScale).sp),
    bodyMedium = TextStyle(fontFamily = AppDefaultFont, fontWeight = FontWeight.Normal, fontSize = (14 * fontScale).sp, lineHeight = (20 * fontScale).sp),
    bodySmall = TextStyle(fontFamily = AppDefaultFont, fontWeight = FontWeight.Normal, fontSize = (12 * fontScale).sp, lineHeight = (18 * fontScale).sp),
    labelLarge = TextStyle(fontFamily = AppDefaultFont, fontWeight = FontWeight.Medium, fontSize = (14 * fontScale).sp),
    labelMedium = TextStyle(fontFamily = AppDefaultFont, fontWeight = FontWeight.Medium, fontSize = (12 * fontScale).sp),
    labelSmall = TextStyle(fontFamily = AppDefaultFont, fontWeight = FontWeight.Medium, fontSize = (10 * fontScale).sp),
)

val MoTypography = buildTypography()
