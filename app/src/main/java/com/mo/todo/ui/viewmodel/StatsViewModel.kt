package com.mo.todo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mo.todo.repository.MemoRepository
import com.mo.todo.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    todoRepository: TodoRepository,
    memoRepository: MemoRepository
) : ViewModel() {

    val todoTotal: StateFlow<Int> = todoRepository.getTotalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todoCompleted: StateFlow<Int> = todoRepository.getCompletedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todoActive: StateFlow<Int> = todoRepository.getActiveCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val memoTotal: StateFlow<Int> = memoRepository.getTotalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val memoStarred: StateFlow<Int> = memoRepository.getStarredCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completionRate: StateFlow<Int> = combine(todoTotal, todoCompleted) { total, completed ->
        if (total == 0) 0 else (completed * 100 / total)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
