package com.motut.mo.data

import androidx.compose.runtime.Stable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Stable
data class Todo(
    val id: Long,
    val title: String,
    val content: String = "",
    val location: String = "",
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

