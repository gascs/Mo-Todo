package com.motut.mo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motut.mo.MoApplication
import com.motut.mo.data.Memo
import com.motut.mo.data.MemoCategory
import com.motut.mo.data.Priority
import com.motut.mo.data.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class AppViewModel : ViewModel() {

    private val dbHelper = MoApplication.instance.databaseHelper

    private val _memos = MutableStateFlow<List<Memo>>(emptyList())
    val memos: StateFlow<List<Memo>> = _memos.asStateFlow()

    private val _selectedMemoIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedMemoIds: StateFlow<Set<Long>> = _selectedMemoIds.asStateFlow()

    private val memoMap = mutableMapOf<Long, Memo>()

    fun getSortedMemos(memos: List<Memo>): List<Memo> {
        return memos.sortedWith(compareByDescending<Memo> { !it.isPinned }.thenByDescending { it.createdAt })
    }

    private val _categories = MutableStateFlow<List<MemoCategory>>(emptyList())
    val categories: StateFlow<List<MemoCategory>> = _categories.asStateFlow()

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    private val _selectedTodoIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedTodoIds: StateFlow<Set<Long>> = _selectedTodoIds.asStateFlow()

    private val todoMap = mutableMapOf<Long, Todo>()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _categories.value = dbHelper.getAllCategories()
            val memoList = dbHelper.getAllMemos()
            val todoList = dbHelper.getAllTodos()
            memoMap.clear()
            todoMap.clear()
            memoList.forEach { memoMap[it.id] = it }
            todoList.forEach { todoMap[it.id] = it }
            _memos.value = memoList
            _todos.value = todoList
        }
    }

    fun addMemo(title: String, content: String, categoryId: Long? = null) {
        viewModelScope.launch {
            val newMemo = Memo(
                id = 0L,
                title = title,
                content = content,
                categoryId = categoryId
            )
            val id = dbHelper.insertMemo(newMemo)
            val memoWithId = newMemo.copy(id = id)
            memoMap[id] = memoWithId
            _memos.value = listOf(memoWithId) + _memos.value
        }
    }

    fun updateMemo(id: Long, title: String, content: String, categoryId: Long? = null, isPinned: Boolean? = null) {
        viewModelScope.launch {
            val existingMemo = memoMap[id] ?: return@launch
            val updatedMemo = existingMemo.copy(
                title = title,
                content = content,
                categoryId = categoryId ?: existingMemo.categoryId,
                isPinned = isPinned ?: existingMemo.isPinned,
                updatedAt = LocalDateTime.now()
            )
            dbHelper.updateMemo(updatedMemo)
            memoMap[id] = updatedMemo
            _memos.value = _memos.value.map { if (it.id == id) updatedMemo else it }
        }
    }

    fun toggleMemoPin(id: Long) {
        viewModelScope.launch {
            val existingMemo = memoMap[id] ?: return@launch
            val updatedMemo = existingMemo.copy(
                isPinned = !existingMemo.isPinned,
                updatedAt = LocalDateTime.now()
            )
            dbHelper.updateMemo(updatedMemo)
            memoMap[id] = updatedMemo
            _memos.value = _memos.value.map { if (it.id == id) updatedMemo else it }
        }
    }

    fun deleteMemo(id: Long) {
        viewModelScope.launch {
            dbHelper.deleteMemo(id)
            memoMap.remove(id)
            _memos.value = _memos.value.filterNot { it.id == id }
            _selectedMemoIds.value -= id
        }
    }

    fun toggleMemoSelection(id: Long) {
        viewModelScope.launch {
            _selectedMemoIds.value = if (_selectedMemoIds.value.contains(id)) {
                _selectedMemoIds.value - id
            } else {
                _selectedMemoIds.value + id
            }
        }
    }

    fun selectAllMemos() {
        viewModelScope.launch {
            _selectedMemoIds.value = memoMap.keys
        }
    }

    fun clearMemoSelection() {
        viewModelScope.launch {
            _selectedMemoIds.value = emptySet()
        }
    }

    fun deleteSelectedMemos() {
        viewModelScope.launch {
            val toDelete = _selectedMemoIds.value.toList()
            toDelete.forEach { 
                dbHelper.deleteMemo(it) 
                memoMap.remove(it)
            }
            _memos.value = _memos.value.filterNot { memo -> toDelete.contains(memo.id) }
            _selectedMemoIds.value = emptySet()
        }
    }

    fun addTodo(
        title: String,
        content: String = "",
        location: String = "",
        date: LocalDate? = null,
        time: LocalTime? = null,
        priority: Priority = Priority.MEDIUM
    ) {
        viewModelScope.launch {
            val newTodo = Todo(
                id = 0L,
                title = title,
                content = content,
                location = location,
                date = date,
                time = time,
                priority = priority
            )
            val id = dbHelper.insertTodo(newTodo)
            val todoWithId = newTodo.copy(id = id)
            todoMap[id] = todoWithId
            _todos.value = listOf(todoWithId) + _todos.value
        }
    }

    fun toggleTodoCompletion(id: Long) {
        viewModelScope.launch {
            val existingTodo = todoMap[id] ?: return@launch
            val updatedTodo = existingTodo.copy(
                isCompleted = !existingTodo.isCompleted,
                updatedAt = LocalDateTime.now()
            )
            dbHelper.updateTodo(updatedTodo)
            todoMap[id] = updatedTodo
            _todos.value = _todos.value.map { if (it.id == id) updatedTodo else it }
        }
    }

    fun deleteTodo(id: Long) {
        viewModelScope.launch {
            dbHelper.deleteTodo(id)
            todoMap.remove(id)
            _todos.value = _todos.value.filterNot { it.id == id }
            _selectedTodoIds.value -= id
        }
    }

    fun toggleTodoSelection(id: Long) {
        viewModelScope.launch {
            _selectedTodoIds.value = if (_selectedTodoIds.value.contains(id)) {
                _selectedTodoIds.value - id
            } else {
                _selectedTodoIds.value + id
            }
        }
    }

    fun selectAllTodos() {
        viewModelScope.launch {
            _selectedTodoIds.value = todoMap.keys
        }
    }

    fun clearTodoSelection() {
        viewModelScope.launch {
            _selectedTodoIds.value = emptySet()
        }
    }

    fun deleteSelectedTodos() {
        viewModelScope.launch {
            val toDelete = _selectedTodoIds.value.toList()
            toDelete.forEach { 
                dbHelper.deleteTodo(it) 
                todoMap.remove(it)
            }
            _todos.value = _todos.value.filterNot { todo -> toDelete.contains(todo.id) }
            _selectedTodoIds.value = emptySet()
        }
    }
}
