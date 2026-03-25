package com.motut.mo.repository

import com.motut.mo.data.Memo
import com.motut.mo.data.MemoEntity
import com.motut.mo.data.toMemo
import com.motut.mo.data.toMemoEntity
import com.motut.mo.database.MemoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class MemoRepository(private val memoDao: MemoDao) {

    val allMemos: Flow<List<Memo>> = memoDao.getAllMemos().map { entities ->
        entities.map { it.toMemo() }
    }

    val pinnedMemos: Flow<List<Memo>> = memoDao.getPinnedMemos().map { entities ->
        entities.map { it.toMemo() }
    }

    fun getSortedMemos(memos: List<Memo>): List<Memo> {
        return memos.sortedWith(compareByDescending<Memo> { !it.isPinned }.thenByDescending { it.createdAt })
    }

    suspend fun getMemoById(id: Long): Memo? = withContext(Dispatchers.IO) {
        memoDao.getMemoById(id)?.toMemo()
    }

    suspend fun addMemo(
        title: String,
        content: String,
        categoryId: Long? = null
    ): Long = withContext(Dispatchers.IO) {
        val memo = MemoEntity(
            title = title,
            content = content,
            categoryId = categoryId
        )
        memoDao.insertMemo(memo)
    }

    suspend fun updateMemo(
        id: Long,
        title: String,
        content: String,
        categoryId: Long? = null
    ) = withContext(Dispatchers.IO) {
        val existingMemo = memoDao.getMemoById(id)
        existingMemo?.let {
            val updatedMemo = it.copy(
                title = title,
                content = content,
                categoryId = categoryId ?: it.categoryId,
                updatedAt = LocalDateTime.now()
            )
            memoDao.updateMemo(updatedMemo)
        }
    }

    suspend fun toggleMemoPin(id: Long) = withContext(Dispatchers.IO) {
        val memo = memoDao.getMemoById(id)
        memo?.let {
            memoDao.togglePin(id, !it.isPinned)
        }
    }

    suspend fun deleteMemo(id: Long) = withContext(Dispatchers.IO) {
        memoDao.deleteMemoById(id)
    }
}
