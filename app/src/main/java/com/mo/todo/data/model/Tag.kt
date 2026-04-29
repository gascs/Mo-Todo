package com.mo.todo.data.model

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
}
