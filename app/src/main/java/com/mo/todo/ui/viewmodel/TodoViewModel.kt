package com.mo.todo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mo.todo.data.model.Todo
import com.mo.todo.repository.TodoRepository
import com.mo.todo.worker.ReminderWorker
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
    private val _selectedTag = MutableStateFlow("all")
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
                }.onFailure { _errorChannel.send("保存失败: ${it.message}") }
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.updateTodo(todo)
                .onSuccess {
                    if (todo.reminderTime != null && todo.reminderTime > System.currentTimeMillis())
                        scheduleReminder(todo.id, todo.title, todo.reminderTime)
                    else if (todo.reminderTime == null) cancelReminder(todo.id)
                }.onFailure { _errorChannel.send("更新失败: ${it.message}") }
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todo)
                .onSuccess { cancelReminder(todo.id) }
                .onFailure { _errorChannel.send("删除失败: ${it.message}") }
        }
    }

    fun toggleCompletion(id: Long, completed: Boolean) {
        viewModelScope.launch {
            todoRepository.updateCompletion(id, completed)
                .onSuccess { if (completed) cancelReminder(id) }
                .onFailure { _errorChannel.send("操作失败: ${it.message}") }
        }
    }

    suspend fun getTodoById(id: Long): Todo? = todoRepository.getTodoById(id).getOrNull()

    suspend fun updateTagByTag(oldTag: String, newTag: String): Int =
        todoRepository.updateTagByTag(oldTag, newTag).getOrDefault(0)

    private fun scheduleReminder(todoId: Long, title: String, reminderTime: Long) {
        val delay = reminderTime - System.currentTimeMillis()
        if (delay <= 0) return
        val inputData = Data.Builder().putLong("todo_id", todoId).putString("todo_title", title).build()
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("todo_reminder_$todoId")
            .build()
        WorkManager.getInstance(application)
            .enqueueUniqueWork("reminder_$todoId", ExistingWorkPolicy.REPLACE, workRequest)
    }

    private fun cancelReminder(todoId: Long) {
        WorkManager.getInstance(application).cancelAllWorkByTag("todo_reminder_$todoId")
    }
}
