package com.mo.todo.repository

import com.mo.todo.data.dao.MemoDao
import com.mo.todo.data.model.Memo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoRepository @Inject constructor(
    private val memoDao: MemoDao
) {
    fun getAllMemos(): Flow<List<Memo>> = memoDao.getAllMemos()
    suspend fun getAllMemosSuspend(): List<Memo> = memoDao.getAllMemosSuspend()
    fun getMemosByTag(tag: String): Flow<List<Memo>> = memoDao.getMemosByTag(tag)
    fun searchMemos(query: String): Flow<List<Memo>> = memoDao.searchMemos(query)
    fun getStarredMemos(): Flow<List<Memo>> = memoDao.getStarredMemos()
    suspend fun insertMemo(memo: Memo): Result<Long> = runCatching { memoDao.insertMemo(memo) }
    suspend fun updateMemo(memo: Memo): Result<Unit> = runCatching { memoDao.updateMemo(memo) }
    suspend fun deleteMemo(memo: Memo): Result<Unit> = runCatching { memoDao.deleteMemo(memo) }
    suspend fun deleteMemoById(id: Long): Result<Unit> = runCatching { memoDao.deleteMemoById(id) }
    suspend fun updateStarred(id: Long, isStarred: Boolean): Result<Unit> = runCatching { memoDao.updateStarred(id, isStarred) }
    suspend fun getMemoById(id: Long): Result<Memo?> = runCatching { memoDao.getMemoById(id) }
    suspend fun updateTagByTag(oldTag: String, newTag: String): Result<Int> = runCatching { memoDao.updateTagByTag(oldTag, newTag) }
    fun getTotalCount(): Flow<Int> = memoDao.getTotalCount()
    fun getStarredCount(): Flow<Int> = memoDao.getStarredCount()
}
