package com.motut.mo.data

import androidx.compose.runtime.Stable
import java.time.LocalDateTime

@Stable
data class MemoCategory(
    val id: Long,
    val name: String,
    val color: Long
)

@Stable
data class Attachment(
    val id: Long = 0L,
    val memoId: Long,
    val type: AttachmentType,
    val uri: String,
    val fileName: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class AttachmentType {
    IMAGE,
    AUDIO,
    FILE
}

@Stable
data class Memo(
    val id: Long,
    val title: String,
    val content: String,
    val categoryId: Long? = null,
    val isPinned: Boolean = false,
    val attachments: List<Attachment> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
