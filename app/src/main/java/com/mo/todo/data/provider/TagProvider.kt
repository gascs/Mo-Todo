package com.mo.todo.data.provider

import com.mo.todo.data.model.Tag

interface TagProvider {
    fun getTodoTags(): List<Tag>
    fun getMemoTags(): List<Tag>
}
