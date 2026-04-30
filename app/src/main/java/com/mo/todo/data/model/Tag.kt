package com.mo.todo.data.model

import androidx.annotation.StringRes
import com.mo.todo.R

data class Tag(
    val key: String,
    val label: String
)

object TagConfig {
    val todoTags = listOf(
        Tag("all", "全部"),
        Tag("work", "工作"),
        Tag("personal", "个人"),
        Tag("shopping", "购物")
    )

    val memoTags = listOf(
        Tag("all", "全部"),
        Tag("note", "便签"),
        Tag("reading", "阅读笔记"),
        Tag("project", "项目")
    )

    @StringRes
    fun displayNameResId(key: String): Int = when (key) {
        "all" -> R.string.tag_all
        "work" -> R.string.tag_work
        "personal" -> R.string.tag_personal
        "shopping" -> R.string.tag_shopping
        "note" -> R.string.tag_note
        "reading" -> R.string.tag_reading
        "project" -> R.string.tag_project
        else -> 0
    }

    @StringRes
    fun labelDisplayNameResId(label: String): Int = when (label) {
        "全部" -> R.string.tag_all
        "工作" -> R.string.tag_work
        "个人" -> R.string.tag_personal
        "购物" -> R.string.tag_shopping
        "便签" -> R.string.tag_note
        "阅读笔记" -> R.string.tag_reading
        "项目" -> R.string.tag_project
        else -> 0
    }

    @StringRes
    fun memoColorDisplayNameResId(colorKey: String): Int = when (colorKey) {
        "默认" -> R.string.memo_color_default
        "薰衣草" -> R.string.memo_color_lavender
        "薄荷" -> R.string.memo_color_mint
        "珊瑚" -> R.string.memo_color_coral
        "琥珀" -> R.string.memo_color_amber
        "天空" -> R.string.memo_color_sky
        "玫瑰" -> R.string.memo_color_rose
        else -> 0
    }
}
