package com.mo.todo.repository

import com.mo.todo.data.dao.TodoDao
import com.mo.todo.data.model.Todo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    fun getAllTodos(): Flow<List<Todo>> = todoDao.getAllTodos()

    fun getTodosByCompletion(completed: Boolean): Flow<List<Todo>> =
        todoDao.getTodosByCompletion(completed)

    fun getActiveTodosByTag(tag: String): Flow<List<Todo>> =
        todoDao.getActiveTodosByTag(tag)

    fun getCompletedTodosByTag(tag: String): Flow<List<Todo>> =
        todoDao.getCompletedTodosByTag(tag)

    fun searchTodos(query: String): Flow<List<Todo>> = todoDao.searchTodos(query)

    suspend fun insertTodo(todo: Todo): Long = todoDao.insertTodo(todo)

    suspend fun updateTodo(todo: Todo) = todoDao.updateTodo(todo)

    suspend fun deleteTodo(todo: Todo) = todoDao.deleteTodo(todo)

    suspend fun deleteTodoById(id: Long) = todoDao.deleteTodoById(id)

    suspend fun updateCompletion(id: Long, completed: Boolean) =
        todoDao.updateCompletion(id, completed)

    suspend fun getTodoById(id: Long): Todo? = todoDao.getTodoById(id)

    suspend fun getUpcomingReminders(now: Long, threshold: Long): List<Todo> =
        todoDao.getUpcomingReminders(now, threshold)
}
