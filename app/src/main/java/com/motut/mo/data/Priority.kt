package com.motut.mo.data

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.motut.mo.ui.theme.AppColors

/**
 * 优先级扩展属性 - 统一优先级颜色映射
 * 解决之前颜色值散落在多文件的问题（硬编码 vs Theme vs AppColors）
 *
 * 使用方式：
 * - Composable中: priority.color / priority.displayColor
 * - 非UI代码: priority.name (原有enum name)
 */
enum class Priority(val displayName: String) {
    LOW("低"),
    MEDIUM("中"),
    HIGH("高")
}

/**
 * 获取优先级的主题色（用于普通UI场景）
 * 基于Material Theme语义色，自动适配亮/暗色主题
 */
@Composable
fun Priority.themeColor(): Color = when (this) {
    Priority.HIGH -> MaterialTheme.colorScheme.error
    Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
    Priority.LOW -> MaterialTheme.colorScheme.secondary
}

/**
 * 获取优先级的品牌色（用于卡片、标签等需要固定颜色的场景）
 * 使用AppColors中定义的Mo-Todo品牌色，不随主题变化
 */
val Priority.brandColor: Color get() = when (this) {
    Priority.HIGH -> AppColors.PriorityHighModern
    Priority.MEDIUM -> AppColors.PriorityMediumModern
    Priority.LOW -> AppColors.PriorityLowModern
}
