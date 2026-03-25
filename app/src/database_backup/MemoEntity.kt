package com.motut.mo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "memos")
data class MemoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val content: String,
    val categoryId: Long? = null,
    val isPinned: Boolean = false,
    val color: Int? = null,
    val isLocked: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

fun MemoEntity.toMemo(): Memo = Memo(
    id = id,
    title = title,
    content = content,
    categoryId = categoryId,
    isPinned = isPinned,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Memo.toMemoEntity(): MemoEntity = MemoEntity(
    id = id,
    title = title,
    content = content,
    categoryId = categoryId,
    isPinned = isPinned,
    createdAt = createdAt,
    updatedAt = updatedAt
)
