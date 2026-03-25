package com.motut.mo.database

import androidx.room.*
import com.motut.mo.data.MemoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    @Query("SELECT * FROM memos ORDER BY isPinned DESC, createdAt DESC")
    fun getAllMemos(): Flow<List<MemoEntity>>

    @Query("SELECT * FROM memos WHERE isPinned = 1 ORDER BY createdAt DESC")
    fun getPinnedMemos(): Flow<List<MemoEntity>>

    @Query("SELECT * FROM memos WHERE id = :id")
    suspend fun getMemoById(id: Long): MemoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemo(memo: MemoEntity): Long

    @Update
    suspend fun updateMemo(memo: MemoEntity)

    @Delete
    suspend fun deleteMemo(memo: MemoEntity)

    @Query("DELETE FROM memos WHERE id = :id")
    suspend fun deleteMemoById(id: Long)

    @Query("UPDATE memos SET isPinned = :isPinned, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun togglePin(id: Long, isPinned: Boolean)
}
