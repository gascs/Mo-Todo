package com.mo.todo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mo.todo.data.model.Memo
import com.mo.todo.repository.MemoRepository
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
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val memoRepository: MemoRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private val _selectedTag = MutableStateFlow("all")
    val selectedTag: StateFlow<String> = _selectedTag.asStateFlow()
    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()
    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    private val _errorChannel = Channel<String>(Channel.BUFFERED)
    val errorEvents = _errorChannel.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val memos: StateFlow<List<Memo>> = combine(_selectedTag, _searchQuery) { tag, query -> Pair(tag, query) }
        .flatMapLatest { (tag, query) ->
            if (query.isNotBlank()) memoRepository.searchMemos(query)
            else if (tag == "all") memoRepository.getAllMemos()
            else memoRepository.getMemosByTag(tag)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setSelectedTag(tag: String) { _selectedTag.value = tag }
    fun setSearchActive(active: Boolean) { _isSearchActive.value = active; if (!active) _searchQuery.value = "" }
    fun toggleViewMode() { _isGridView.value = !_isGridView.value }

    fun insertMemo(memo: Memo) {
        viewModelScope.launch {
            memoRepository.insertMemo(memo)
                .onFailure { _errorChannel.send("保存失败: ${it.message}") }
        }
    }

    fun updateMemo(memo: Memo) {
        viewModelScope.launch {
            memoRepository.updateMemo(memo)
                .onFailure { _errorChannel.send("更新失败: ${it.message}") }
        }
    }

    fun deleteMemo(memo: Memo) {
        viewModelScope.launch {
            memoRepository.deleteMemo(memo)
                .onFailure { _errorChannel.send("删除失败: ${it.message}") }
        }
    }

    fun toggleStarred(id: Long, isStarred: Boolean) {
        viewModelScope.launch {
            memoRepository.updateStarred(id, isStarred)
                .onFailure { _errorChannel.send("操作失败: ${it.message}") }
        }
    }

    suspend fun getMemoById(id: Long): Memo? = memoRepository.getMemoById(id).getOrNull()
    suspend fun updateTagByTag(oldTag: String, newTag: String): Int =
        memoRepository.updateTagByTag(oldTag, newTag).getOrDefault(0)
}
