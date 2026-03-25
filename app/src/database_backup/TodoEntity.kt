package com.motut.mo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val content: String = "",
    val location: String = "",
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val order: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

fun TodoEntity.toTodo(): Todo = Todo(
    id = id,
    title = title,
    content = content,
    location = location,
    date = date,
    time = time,
    priority = priority,
    isCompleted = isCompleted,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Todo.toTodoEntity(): TodoEntity = TodoEntity(
    id = id,
    title = title,
    content = content,
    location = location,
    date = date,
    time = time,
    priority = priority,
    isCompleted = isCompleted,
    createdAt = createdAt,
    updatedAt = updatedAt
)
