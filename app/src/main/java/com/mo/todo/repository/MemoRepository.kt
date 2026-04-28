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

    fun getMemosByTag(tag: String): Flow<List<Memo>> = memoDao.getMemosByTag(tag)

    fun searchMemos(query: String): Flow<List<Memo>> = memoDao.searchMemos(query)

    fun getStarredMemos(): Flow<List<Memo>> = memoDao.getStarredMemos()

    suspend fun insertMemo(memo: Memo): Long = memoDao.insertMemo(memo)

    suspend fun updateMemo(memo: Memo) = memoDao.updateMemo(memo)

    suspend fun deleteMemo(memo: Memo) = memoDao.deleteMemo(memo)

    suspend fun deleteMemoById(id: Long) = memoDao.deleteMemoById(id)

    suspend fun updateStarred(id: Long, isStarred: Boolean) =
        memoDao.updateStarred(id, isStarred)

    suspend fun getMemoById(id: Long): Memo? = memoDao.getMemoById(id)
}
