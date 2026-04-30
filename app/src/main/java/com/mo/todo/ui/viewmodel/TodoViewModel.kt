package com.mo.todo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mo.todo.data.model.Todo
import com.mo.todo.R
import com.mo.todo.repository.TodoRepository
import com.mo.todo.worker.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val application: Application
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _selectedTag = MutableStateFlow("全部")
    val selectedTag: StateFlow<String> = _selectedTag.asStateFlow()
    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _errorChannel = Channel<String>(Channel.BUFFERED)
    val errorEvents = _errorChannel.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeTodos: StateFlow<List<Todo>> = combine(_selectedTag, _searchQuery) { tag, query -> Pair(tag, query) }
        .flatMapLatest { (tag, query) ->
            if (query.isNotBlank()) todoRepository.searchTodos(query)
            else todoRepository.getActiveTodosByTag(tag)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val completedTodos: StateFlow<List<Todo>> = _selectedTag.flatMapLatest { todoRepository.getCompletedTodosByTag(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setSelectedTag(tag: String) { _selectedTag.value = tag }
    fun setSearchActive(active: Boolean) { _isSearchActive.value = active; if (!active) _searchQuery.value = "" }

    fun insertTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.insertTodo(todo)
                .onSuccess { id ->
                    if (id > 0 && todo.reminderTime != null && todo.reminderTime > System.currentTimeMillis())
                        scheduleReminder(id, todo.title, todo.reminderTime)
                }.onFailure { _errorChannel.send(application.getString(R.string.error_save_failed, it.message ?: "")) }
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.updateTodo(todo)
                .onSuccess {
                    if (todo.reminderTime != null && todo.reminderTime > System.currentTimeMillis())
                        scheduleReminder(todo.id, todo.title, todo.reminderTime)
                    else if (todo.reminderTime == null) cancelReminder(todo.id)
                }.onFailure { _errorChannel.send(application.getString(R.string.error_update_failed, it.message ?: "")) }
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todo)
                .onSuccess { cancelReminder(todo.id) }
                .onFailure { _errorChannel.send(application.getString(R.string.error_delete_failed, it.message ?: "")) }
        }
    }

    fun toggleCompletion(id: Long, completed: Boolean) {
        viewModelScope.launch {
            todoRepository.updateCompletion(id, completed)
                .onSuccess { if (completed) cancelReminder(id) }
                .onFailure { _errorChannel.send(application.getString(R.string.error_operation_failed, it.message ?: "")) }
        }
    }

    suspend fun getTodoById(id: Long): Todo? = todoRepository.getTodoById(id).getOrNull()

    fun deleteTodoById(id: Long) {
        viewModelScope.launch {
            todoRepository.deleteTodoById(id)
                .onSuccess { cancelReminder(id) }
                .onFailure { _errorChannel.send(application.getString(R.string.error_delete_failed, it.message ?: "")) }
        }
    }

    suspend fun updateTagByTag(oldTag: String, newTag: String): Int =
        todoRepository.updateTagByTag(oldTag, newTag).getOrDefault(0)

    suspend fun deleteByTag(tag: String) {
        todoRepository.deleteByTag(tag)
    }

    suspend fun renameTag(oldTag: String, newTag: String) {
        todoRepository.updateTagByTag(oldTag, newTag)
    }

    suspend fun exportTodosJson(): String {
        val todos = todoRepository.getAllTodosSuspend()
        val jsonArray = org.json.JSONArray()
        todos.forEach { todo ->
            val obj = org.json.JSONObject().apply {
                put("id", todo.id)
                put("title", todo.title)
                put("description", todo.description ?: "")
                put("tag", todo.tag)
                put("priority", todo.priority)
                put("reminderTime", todo.reminderTime ?: org.json.JSONObject.NULL)
                put("isCompleted", todo.isCompleted)
                put("createdAt", todo.createdAt)
            }
            jsonArray.put(obj)
        }
        return org.json.JSONObject().apply {
            put("version", "1.0")
            put("exportTime", System.currentTimeMillis())
            put("todos", jsonArray)
        }.toString(2)
    }

    suspend fun importTodosJson(json: String): Int {
        val jsonArray = org.json.JSONObject(json).getJSONArray("todos")
        var count = 0
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val todo = Todo(
                title = obj.getString("title"),
                description = obj.optString("description", "").ifBlank { null },
                tag = obj.optString("tag", "工作"),
                priority = obj.optInt("priority", 1),
                reminderTime = if (obj.isNull("reminderTime")) null else obj.optLong("reminderTime"),
                isCompleted = obj.optBoolean("isCompleted", false),
                createdAt = obj.optLong("createdAt", System.currentTimeMillis())
            )
            todoRepository.insertTodo(todo)
            count++
        }
        return count
    }

    private fun scheduleReminder(todoId: Long, title: String, reminderTime: Long) {
        ReminderScheduler.schedule(application, todoId, title, reminderTime)
    }

    private fun cancelReminder(todoId: Long) {
        ReminderScheduler.cancel(application, todoId)
    }
}
