package com.mo.todo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memos")
data class Memo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String = "",
    val tag: String = "note",
    val color: Int? = null,
    val isStarred: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
