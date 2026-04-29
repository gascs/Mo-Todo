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
    suspend fun getAllTodosSuspend(): List<Todo> = todoDao.getAllTodosSuspend()
    fun getTodosByCompletion(completed: Boolean): Flow<List<Todo>> = todoDao.getTodosByCompletion(completed)
    fun getActiveTodosByTag(tag: String): Flow<List<Todo>> = todoDao.getActiveTodosByTag(tag)
    fun getCompletedTodosByTag(tag: String): Flow<List<Todo>> = todoDao.getCompletedTodosByTag(tag)
    fun searchTodos(query: String): Flow<List<Todo>> = todoDao.searchTodos(query)
    suspend fun insertTodo(todo: Todo): Result<Long> = runCatching { todoDao.insertTodo(todo) }
    suspend fun updateTodo(todo: Todo): Result<Unit> = runCatching { todoDao.updateTodo(todo) }
    suspend fun deleteTodo(todo: Todo): Result<Unit> = runCatching { todoDao.deleteTodo(todo) }
    suspend fun deleteTodoById(id: Long): Result<Unit> = runCatching { todoDao.deleteTodoById(id) }
    suspend fun updateCompletion(id: Long, completed: Boolean): Result<Unit> = runCatching { todoDao.updateCompletion(id, completed) }
    suspend fun getTodoById(id: Long): Result<Todo?> = runCatching { todoDao.getTodoById(id) }
    suspend fun getUpcomingReminders(now: Long, threshold: Long): List<Todo> = todoDao.getUpcomingReminders(now, threshold)
    suspend fun updateTagByTag(oldTag: String, newTag: String): Result<Int> = runCatching { todoDao.updateTagByTag(oldTag, newTag) }
}
