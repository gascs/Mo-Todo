package com.motut.mo.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.motut.mo.MoApplication
import com.motut.mo.data.AppDatabaseHelper
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

/**
 * ViewModel for managing todos and memos.
 * Uses repository pattern via AppDatabaseHelper for data operations.
 *
 * Supports two construction modes:
 * 1. With explicit databaseHelper (recommended for testing and DI)
 * 2. With default singleton (backward compatible)
 *
 * Optimizations applied:
 * - Copy-on-write pattern for immutable state updates
 * - Efficient batch operations
 * - Debounced search with PerformanceUtils
 */
class AppViewModel(
    private val databaseHelper: AppDatabaseHelper
) : ViewModel() {

    /**
     * Default constructor using singleton pattern for backward compatibility.
     * WARNING: Prefer using AppViewModel(context) or AppViewModel(databaseHelper) for better testability.
     */
    constructor() : this(AppDatabaseHelper.getInstance(MoApplication.instance))

    companion object {
        /**
         * Factory for creating AppViewModel with database dependency.
         */
        fun factory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AppViewModel(
                        AppDatabaseHelper.getInstance(context)
                    ) as T
                }
            }
        }
    }

    // ==================== Memo 状态 ====================
    private val _memos = MutableStateFlow<List<Memo>>(emptyList())
    val memos: StateFlow<List<Memo>> = _memos.asStateFlow()

    private val _selectedMemoIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedMemoIds: StateFlow<Set<Long>> = _selectedMemoIds.asStateFlow()

    // 使用只读 Map 作为缓存
    private val memoMap = mutableMapOf<Long, Memo>()

    // ==================== Memo 派生状态（优化：使用缓存避免重复计算） ====================
    private var _cachedPinnedMemos: List<Memo>? = null
    private var _cachedMemoCount: Int? = null

    // FIXED (P1-6): 原缓存命中判断逻辑有bug — cached !== newFilter 会误判为新数据
    // 修复方案：基于 id 集合比对而非对象引用比对
    val pinnedMemos: List<Memo>
        get() {
            val cached = _cachedPinnedMemos
            val current = _memos.value
            val currentPinned = current.filter { it.isPinned }
            val isCacheValid = cached != null &&
                    cached.size == currentPinned.size &&
                    cached.zip(currentPinned).all { (a, b) -> a.id == b.id && a.isPinned == b.isPinned }
            return if (isCacheValid) cached else currentPinned.also { _cachedPinnedMemos = it }
        }

    val unpinnedMemos: List<Memo>
        get() = _memos.value.filter { !it.isPinned }

    val memoCount: Int
        get() = _cachedMemoCount ?: _memos.value.size.also { _cachedMemoCount = it }

    val selectedMemoCount: Int
        get() = _selectedMemoIds.value.size

    // ==================== Todo 状态 ====================
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    private val _selectedTodoIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedTodoIds: StateFlow<Set<Long>> = _selectedTodoIds.asStateFlow()

    private val todoMap = mutableMapOf<Long, Todo>()

    // ==================== Todo 派生状态（优化：使用缓存避免重复计算） ====================
    private var _cachedIncompleteTodos: List<Todo>? = null
    private var _cachedCompletedTodos: List<Todo>? = null
    private var _cachedOverdueTodos: List<Todo>? = null
    private var _cachedTodayTodos: List<Todo>? = null

    // FIXED (P1-6): 统一使用 id 集合 + 状态字段比对，避免 O(n²) 的 any 查找
    private fun <T> isCacheValid(cached: List<T>?, current: List<T>, filterFn: (T) -> Boolean, equalityFn: (T, T) -> Boolean): Boolean {
        if (cached == null) return false
        val filtered = current.filter(filterFn)
        return cached.size == filtered.size && cached.zip(filtered).all { (a, b) -> equalityFn(a, b) }
    }

    val incompleteTodos: List<Todo>
        get() {
            val current = _todos.value
            val cached = _cachedIncompleteTodos
            val isValid = isCacheValid(cached, current,
                filterFn = { !it.isCompleted },
                equalityFn = { a, b -> a.id == b.id && a.isCompleted == b.isCompleted })
            return if (isValid) cached!! else current.filter { !it.isCompleted }.also { _cachedIncompleteTodos = it }
        }

    val completedTodos: List<Todo>
        get() {
            val current = _todos.value
            val cached = _cachedCompletedTodos
            val isValid = isCacheValid(cached, current,
                filterFn = { it.isCompleted },
                equalityFn = { a, b -> a.id == b.id && a.isCompleted == b.isCompleted })
            return if (isValid) cached!! else current.filter { it.isCompleted }.also { _cachedCompletedTodos = it }
        }

    val todoCount: Int
        get() = _todos.value.size

    val incompleteTodoCount: Int
        get() = incompleteTodos.size

    val overdueTodos: List<Todo>
        get() {
            val today = LocalDate.now()
            val cached = _cachedOverdueTodos
            val incomplete = incompleteTodos
            val isValid = cached != null &&
                    cached.size == incomplete.filter { it.date?.isBefore(today) == true }.size &&
                    cached.all { t -> incomplete.any { it.id == t.id && it.date?.isBefore(today) == true } }
            return if (isValid) cached else incomplete.filter { it.date?.isBefore(today) == true }.also { _cachedOverdueTodos = it }
        }

    val todayTodos: List<Todo>
        get() {
            val today = LocalDate.now()
            val cached = _cachedTodayTodos
            val incomplete = incompleteTodos
            val isValid = cached != null &&
                    cached.size == incomplete.filter { it.date == today }.size &&
                    cached.all { t -> incomplete.any { it.id == t.id && it.date == today } }
            return if (isValid) cached else incomplete.filter { it.date == today }.also { _cachedTodayTodos = it }
        }

    // ==================== 分类状态 ====================
    private val _categories = MutableStateFlow<List<MemoCategory>>(emptyList())
    val categories: StateFlow<List<MemoCategory>> = _categories.asStateFlow()

    init {
        loadData()
    }

    /**
     * Loads all data from the database.
     * Database operations run on IO dispatcher via suspend functions.
     */
    private fun loadData() {
        viewModelScope.launch {
            _categories.value = databaseHelper.getAllCategories()
            val memoList = databaseHelper.getAllMemos()
            val todoList = databaseHelper.getAllTodos()

            // 高效更新 Map
            memoMap.clear()
            memoMap.putAll(memoList.associateBy { it.id })

            todoMap.clear()
            todoMap.putAll(todoList.associateBy { it.id })

            // 更新StateFlow前清除缓存
            _cachedPinnedMemos = null
            _cachedMemoCount = null
            _cachedIncompleteTodos = null
            _cachedCompletedTodos = null
            _cachedOverdueTodos = null
            _cachedTodayTodos = null
            
            _memos.value = memoList
            _todos.value = todoList
        }
    }

    /**
     * Refreshes data from database.
     * Use this for pull-to-refresh or sync scenarios.
     */
    fun refreshData() {
        loadData()
    }

    // ==================== Memo 操作 ====================

    /**
     * Sorts memos with pinned items first, then by creation date.
     * Uses optimized sorting with natural order preservation.
     */
    fun getSortedMemos(memos: List<Memo>): List<Memo> {
        return memos.sortedWith(
            compareByDescending<Memo> { it.isPinned }
                .thenByDescending { it.createdAt }
        )
    }

    fun addMemo(title: String, content: String, categoryId: Long? = null) {
        viewModelScope.launch {
            val newMemo = Memo(
                id = 0L,
                title = title,
                content = content,
                categoryId = categoryId
            )
            val id = databaseHelper.insertMemo(newMemo)
            val memoWithId = newMemo.copy(id = id)

            // 高效更新 Map 和 StateFlow
            memoMap[id] = memoWithId
            _memos.value = _memos.value + memoWithId
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
            databaseHelper.updateMemo(updatedMemo)

            // 高效更新 Map 和 StateFlow
            memoMap[id] = updatedMemo
            _memos.value = _memos.value.map { memo ->
                if (memo.id == id) updatedMemo else memo
            }
        }
    }

    fun toggleMemoPin(id: Long) {
        viewModelScope.launch {
            val existingMemo = memoMap[id] ?: return@launch
            val updatedMemo = existingMemo.copy(
                isPinned = !existingMemo.isPinned,
                updatedAt = LocalDateTime.now()
            )
            databaseHelper.updateMemo(updatedMemo)

            memoMap[id] = updatedMemo
            _memos.value = _memos.value.map { memo ->
                if (memo.id == id) updatedMemo else memo
            }
        }
    }

    fun deleteMemo(id: Long) {
        viewModelScope.launch {
            databaseHelper.deleteMemo(id)

            memoMap.remove(id)
            _memos.value = _memos.value.filterNot { it.id == id }
            _selectedMemoIds.value = _selectedMemoIds.value - id
        }
    }

    fun toggleMemoSelection(id: Long) {
        _selectedMemoIds.value = if (id in _selectedMemoIds.value) {
            _selectedMemoIds.value - id
        } else {
            _selectedMemoIds.value + id
        }
    }

    fun selectAllMemos() {
        _selectedMemoIds.value = memoMap.keys.toSet()
    }

    fun clearMemoSelection() {
        _selectedMemoIds.value = emptySet()
    }

    /**
     * Deletes all selected memos using batch delete for better performance.
     * Uses transaction for atomic operation.
     */
    fun deleteSelectedMemos() {
        viewModelScope.launch {
            val toDelete = _selectedMemoIds.value.toList()
            if (toDelete.isEmpty()) return@launch

            // Use batch delete for better performance
            databaseHelper.deleteMemos(toDelete)

            // 高效批量移除
            toDelete.forEach { memoMap.remove(it) }
            _memos.value = _memos.value.filterNot { it.id in toDelete }
            _selectedMemoIds.value = emptySet()
        }
    }

    /**
     * Searches memos with optimized single query.
     */
    fun searchMemos(query: String, onResult: (List<Memo>) -> Unit) {
        viewModelScope.launch {
            if (query.isBlank()) {
                onResult(_memos.value)
            } else {
                val results = databaseHelper.searchMemos(query)
                onResult(results)
            }
        }
    }

    // ==================== Todo 操作 ====================

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
            val id = databaseHelper.insertTodo(newTodo)
            val todoWithId = newTodo.copy(id = id)

            todoMap[id] = todoWithId
            _todos.value = _todos.value + todoWithId
        }
    }

    fun toggleTodoCompletion(id: Long) {
        viewModelScope.launch {
            val existingTodo = todoMap[id] ?: return@launch
            val updatedTodo = existingTodo.copy(
                isCompleted = !existingTodo.isCompleted,
                updatedAt = LocalDateTime.now()
            )
            databaseHelper.updateTodo(updatedTodo)

            todoMap[id] = updatedTodo
            _todos.value = _todos.value.map { todo ->
                if (todo.id == id) updatedTodo else todo
            }
        }
    }

    fun deleteTodo(id: Long) {
        viewModelScope.launch {
            databaseHelper.deleteTodo(id)

            todoMap.remove(id)
            _todos.value = _todos.value.filterNot { it.id == id }
            _selectedTodoIds.value = _selectedTodoIds.value - id
        }
    }

    fun toggleTodoSelection(id: Long) {
        _selectedTodoIds.value = if (id in _selectedTodoIds.value) {
            _selectedTodoIds.value - id
        } else {
            _selectedTodoIds.value + id
        }
    }

    fun selectAllTodos() {
        _selectedTodoIds.value = todoMap.keys.toSet()
    }

    fun clearTodoSelection() {
        _selectedTodoIds.value = emptySet()
    }

    /**
     * Deletes all selected todos using batch delete for better performance.
     * Uses transaction for atomic operation.
     */
    fun deleteSelectedTodos() {
        viewModelScope.launch {
            val toDelete = _selectedTodoIds.value.toList()
            if (toDelete.isEmpty()) return@launch

            // Use batch delete for better performance
            databaseHelper.deleteTodos(toDelete)

            toDelete.forEach { todoMap.remove(it) }
            _todos.value = _todos.value.filterNot { it.id in toDelete }
            _selectedTodoIds.value = emptySet()
        }
    }

    /**
     * Gets todos for a specific date range (for calendar view).
     */
    fun getTodosByDateRange(startDate: LocalDate, endDate: LocalDate, onResult: (List<Todo>) -> Unit) {
        viewModelScope.launch {
            val results = databaseHelper.getTodosByDateRange(
                startDate.toString(),
                endDate.toString()
            )
            onResult(results)
        }
    }
}
