package com.motut.mo.repository

import com.motut.mo.data.Todo
import com.motut.mo.data.TodoEntity
import com.motut.mo.data.toTodo
import com.motut.mo.data.toTodoEntity
import com.motut.mo.database.TodoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import com.motut.mo.data.Priority

class TodoRepository(private val todoDao: TodoDao) {

    val allTodos: Flow<List<Todo>> = todoDao.getAllTodos().map { entities ->
        entities.map { it.toTodo() }
    }

    val pendingTodos: Flow<List<Todo>> = todoDao.getPendingTodos().map { entities ->
        entities.map { it.toTodo() }
    }

    val completedTodos: Flow<List<Todo>> = todoDao.getCompletedTodos().map { entities ->
        entities.map { it.toTodo() }
    }

    suspend fun getTodoById(id: Long): Todo? = withContext(Dispatchers.IO) {
        todoDao.getTodoById(id)?.toTodo()
    }

    suspend fun addTodo(
        title: String,
        content: String = "",
        location: String = "",
        date: LocalDate? = null,
        time: LocalTime? = null,
        priority: Priority = Priority.MEDIUM
    ): Long = withContext(Dispatchers.IO) {
        val todo = TodoEntity(
            title = title,
            content = content,
            location = location,
            date = date,
            time = time,
            priority = priority,
            isCompleted = false
        )
        todoDao.insertTodo(todo)
    }

    suspend fun updateTodo(
        id: Long,
        title: String,
        content: String,
        location: String,
        date: LocalDate?,
        time: LocalTime?,
        priority: Priority
    ) = withContext(Dispatchers.IO) {
        val existingTodo = todoDao.getTodoById(id)
        existingTodo?.let {
            val updatedTodo = it.copy(
                title = title,
                content = content,
                location = location,
                date = date,
                time = time,
                priority = priority,
                updatedAt = LocalDateTime.now()
            )
            todoDao.updateTodo(updatedTodo)
        }
    }

    suspend fun toggleTodoCompletion(id: Long) = withContext(Dispatchers.IO) {
        val todo = todoDao.getTodoById(id)
        todo?.let {
            todoDao.toggleCompletion(id, !it.isCompleted)
        }
    }

    suspend fun deleteTodo(id: Long) = withContext(Dispatchers.IO) {
        todoDao.deleteTodoById(id)
    }
}
