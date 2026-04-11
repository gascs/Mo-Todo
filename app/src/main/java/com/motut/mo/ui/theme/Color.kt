package com.motut.mo.ui.theme

import androidx.compose.ui.graphics.Color

// ==================== 基础颜色 ====================
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val DarkPrimary = Color(0xFFBB86FC)
val DarkOnPrimary = Color(0xFF000000)

// ==================== 品牌语义颜色 - v2.0 优化版 ====================
object AppColors {
    // 主色调 - 更现代、更活力的 Indigo-Blue 渐变
    val PrimaryLight = Color(0xFF4F46E5)           // Indigo 600 - 更有活力
    val PrimaryDark = Color(0xFF818CF8)            // Indigo 400 - 更清晰
    
    // 优化后的主色调 - 更高对比度、更清晰
    val PrimaryModern = Color(0xFF4F46E5)          // Indigo 600
    val PrimaryLightModern = Color(0xFF6366F1)     // Indigo 500
    val PrimaryDarkModern = Color(0xFF3730A3)       // Indigo 800
    
    // 次要色 - 紫罗兰色系
    val SecondaryModern = Color(0xFF7C3AED)        // Violet 600
    val SecondaryLight = Color(0xFF8B5CF6)          // Violet 500
    
    // 强调色 - 温暖橙色系
    val AccentModern = Color(0xFFF97316)            // Orange 500
    val AccentLight = Color(0xFFFB923C)             // Orange 400

    // 优先级颜色 - v2.0 优化（更鲜明、更易区分）
    val PriorityHigh = Color(0xFFEF4444)            // Red 500 - 更高饱和
    val PriorityMedium = Color(0xFFF59E0B)           // Amber 500
    val PriorityLow = Color(0xFF10B981)             // Emerald 500
    
    // 优先级颜色 - 优化版本
    val PriorityHighModern = Color(0xFFEF4444)      // Red 500
    val PriorityMediumModern = Color(0xFFF59E0B)    // Amber 500
    val PriorityLowModern = Color(0xFF10B981)      // Emerald 500

    // 状态颜色 - v2.0 优化对比度
    val Success = Color(0xFF22C55E)                 // Green 500 - 更鲜明
    val SuccessModern = Color(0xFF16A34A)            // Green 600
    val SuccessLight = Color(0xFF4ADE80)             // Green 400
    val Warning = Color(0xFFF59E0B)                 // Amber 500
    val WarningModern = Color(0xFFEA580C)            // Orange 600
    val WarningLight = Color(0xFFFBBF24)             // Amber 400
    val Error = Color(0xFFEF4444)                    // Red 500
    val ErrorModern = Color(0xFFDC2626)               // Red 600
    val ErrorLight = Color(0xFFF87171)               // Red 400
    val Info = Color(0xFF3B82F6)                    // Blue 500
    val InfoModern = Color(0xFF2563EB)               // Blue 600
    val InfoLight = Color(0xFF60A5FA)               // Blue 400

    // 备忘录分类颜色 - v2.0 优化
    val CategoryWork = Color(0xFF10B981)             // Emerald 500
    val CategoryPersonal = Color(0xFF3B82F6)         // Blue 500
    val CategoryStudy = Color(0xFFF59E0B)            // Amber 500
    val CategoryOther = Color(0xFF8B5CF6)            // Violet 500
    
    // 备忘录分类颜色 - 优化版本
    val CategoryWorkModern = Color(0xFF059669)       // Emerald 600
    val CategoryPersonalModern = Color(0xFF2563EB)  // Blue 600
    val CategoryStudyModern = Color(0xFFD97706)      // Amber 600
    val CategoryOtherModern = Color(0xFF7C3AED)     // Violet 600

    // 渐变色预设 - v2.0 优化（更协调、更有层次感）
    val GradientPrimary = listOf(Color(0xFF4F46E5), Color(0xFF6366F1))     // Indigo渐变
    val GradientSunset = listOf(Color(0xFFF97316), Color(0xFFEC4899))     // Orange to Pink
    val GradientOcean = listOf(Color(0xFF06B6D4), Color(0xFF2563EB))       // Cyan to Blue
    val GradientForest = listOf(Color(0xFF22C55E), Color(0xFF14B8A6))     // Green to Teal
    val GradientSunrise = listOf(Color(0xFFFBBF24), Color(0xFFDC2626))   // Yellow to Red
    val GradientDream = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))      // Violet to Pink
    val GradientMint = listOf(Color(0xFF10B981), Color(0xFF06B6D4))       // Emerald to Cyan
    
    // 玻璃态背景色 - v2.0 优化
    val GlassBackgroundLight = Color(0x80FFFFFF)     // 适中透明度
    val GlassBackgroundDark = Color(0x40000000)     // 适中透明度
    
    // 玻璃态增强版
    val GlassWhite = Color(0x20FFFFFF)              // 更高透明度
    val GlassDark = Color(0x30000000)                // 更高透明度
    val GlassBorderLight = Color(0x40FFFFFF)        // 更柔和边框
    val GlassBorderDark = Color(0x30FFFFFF)          // 更柔和边框

    // 暗色模式专用 - v2.0 优化层次
    val DarkSurface = Color(0xFF1E293B)              // Slate 800
    val DarkBackground = Color(0xFF0F172A)           // Slate 900
    val DarkSurfaceVariant = Color(0xFF334155)       // Slate 700
    
    // 暗色模式增强
    val DarkSurfaceElevated = Color(0xFF334155)     // 抬升表面
    val DarkSurfaceOverlay = Color(0xFF475569)       // 叠加表面
    
    // 统计卡片渐变 - v2.0 优化
    val StatGradientTodo = listOf(Color(0xFF4F46E5), Color(0xFF6366F1))     // Indigo渐变
    val StatGradientCompleted = listOf(Color(0xFF22C55E), Color(0xFF14B8A6)) // Green渐变
    val StatGradientMemo = listOf(Color(0xFFF97316), Color(0xFFFB923C))   // Orange渐变
    
    // 日历特殊日期颜色
    val WeekendColor = Color(0xFFEF4444)              // Red 500
    val HolidayColor = Color(0xFFDC2626)              // Red 600
    val TodayHighlight = Color(0xFF4F46E5)           // 与主色调一致
    
    // 表面颜色 - v2.0 层次感优化
    val SurfaceTintLight = Color(0xFFE0E7FF)         // Indigo 100
    val SurfaceTintDark = Color(0xFF312E81)          // Indigo 900
    
    // 分割线颜色
    val DividerLight = Color(0xFFE2E8F0)              // Slate 200
    val DividerDark = Color(0xFF334155)               // Slate 700
}