package com.mo.todo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mo.todo.data.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    suspend fun getAllTodosSuspend(): List<Todo>

    @Query("SELECT * FROM todos WHERE isCompleted = :completed ORDER BY createdAt DESC")
    fun getTodosByCompletion(completed: Boolean): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE tag = :tag AND isCompleted = :completed ORDER BY createdAt DESC")
    fun getTodosByTagAndCompletion(tag: String, completed: Boolean): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE title LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTodos(query: String): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE isCompleted = 1 AND (tag = :tag OR :tag = 'all') ORDER BY createdAt DESC")
    fun getCompletedTodosByTag(tag: String): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE isCompleted = 0 AND (tag = :tag OR :tag = 'all') ORDER BY priority DESC, createdAt DESC")
    fun getActiveTodosByTag(tag: String): Flow<List<Todo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: Todo): Long

    @Update
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteTodoById(id: Long)

    @Query("UPDATE todos SET isCompleted = :completed WHERE id = :id")
    suspend fun updateCompletion(id: Long, completed: Boolean)

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Long): Todo?

    @Query("SELECT * FROM todos WHERE reminderTime IS NOT NULL AND reminderTime > :now AND reminderTime < :threshold AND isCompleted = 0")
    suspend fun getUpcomingReminders(now: Long, threshold: Long): List<Todo>

    @Query("SELECT * FROM todos WHERE reminderTime IS NOT NULL AND reminderTime > :now AND isCompleted = 0")
    suspend fun getUpcomingRemindersSync(now: Long): List<Todo>

    @Query("UPDATE todos SET tag = :newTag WHERE tag = :oldTag")
    suspend fun updateTagByTag(oldTag: String, newTag: String): Int

    @Query("SELECT COUNT(*) FROM todos")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM todos WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM todos WHERE isCompleted = 0")
    fun getActiveCount(): Flow<Int>

    @Query("SELECT * FROM todos ORDER BY createdAt DESC LIMIT 1")
    fun getLatestTodo(): Flow<Todo?>
}
