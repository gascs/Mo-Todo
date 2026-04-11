package com.motut.mo.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * 统一日期格式化工具类
 * 解决之前日期格式化器散落在5+个文件各自定义的问题
 *
 * 所有日期格式化入口统一在此处，便于：
 * 1. 维护一致性
 * 2. 国际化切换（将来可基于Locale动态调整格式）
 * 3. 减少重复代码
 */
object DateFormats {

    // ==================== 完整日期格式 ====================

    /** 完整中文日期: 2024年01月15日 */
    val fullChinese: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("yyyy年MM月dd日")
    }

    /** ISO标准日期: 2024-01-15 */
    val isoDate: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    /** 带时间的完整格式: 2024-01-15 14:30 */
    val dateTime: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }

    // ==================== 短格式 ====================

    /** 简短日期: 01-15 */
    val shortDate: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("MM-dd")
    }

    /** 中文月份: 2024年01月 */
    val monthYearChinese: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("yyyy年MM月")
    }

    // ==================== 时间格式 ====================

    /** 24小时制时间: 14:30 */
    val time24h: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("HH:mm")
    }

    /** 月日按钮显示: 01月15日 */
    val monthDay: DateTimeFormatter by lazy {
        DateTimeFormatter.ofPattern("MM月dd日")
    }
}

/**
 * 扩展函数：快速格式化 LocalDate 为完整中文日期
 */
fun LocalDate.toFullChinese(): String = this.format(DateFormats.fullChinese)

/**
 * 扩展函数：快速格式化 LocalDate 为ISO短日期
 */
fun LocalDate.toShortDate(): String = this.format(DateFormats.shortDate)

/**
 * 扩展函数：快速格式化 LocalDateTime
 */
fun LocalDateTime.toDateTimeString(): String = this.format(DateFormats.dateTime)

/**
 * 扩展函数：快速格式化 LocalTime
 */
fun LocalTime.toTimeString(): String = this.format(DateFormats.time24h)

/**
 * 获取星期几的中文全称（如"星期三"）
 */
fun LocalDate.dayOfWeekChinese(): String =
    this.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINA)

/**
 * 根据小时数获取时段问候语
 */
fun Int.greeting(): String = when (this) {
    in 0..5 -> "夜深了"
    in 6..11 -> "早上好"
    in 12..17 -> "下午好"
    in 18..21 -> "晚上好"
    else -> "夜深了"
}

/**
 * 根据小时数获取对应表情符号
 */
fun Int.greetingEmoji(): String = when (this) {
    in 6..8 -> "\u2600\ufe0f"  // ☀️
    in 9..11 -> "\ud83c\udf24\ufe0f"  // 🌤️
    in 12..14 -> "\ud83c\udf3b"  // 🌻
    in 15..17 -> "\ud83c\udf24\ufe0f"  // 🌤️
    in 18..20 -> "\ud83c\udf19"  // 🌙
    else -> "\ud83c\udf19"  // 🌙
}
