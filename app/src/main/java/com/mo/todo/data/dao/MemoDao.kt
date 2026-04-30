package com.mo.todo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mo.todo.data.model.Memo
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {

    @Query("SELECT * FROM memos ORDER BY updatedAt DESC")
    fun getAllMemos(): Flow<List<Memo>>

    @Query("SELECT * FROM memos ORDER BY updatedAt DESC")
    suspend fun getAllMemosSuspend(): List<Memo>

    @Query("SELECT * FROM memos WHERE tag = :tag ORDER BY updatedAt DESC")
    fun getMemosByTag(tag: String): Flow<List<Memo>>

    @Query("SELECT * FROM memos WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchMemos(query: String): Flow<List<Memo>>

    @Query("SELECT * FROM memos WHERE isStarred = 1 ORDER BY updatedAt DESC")
    fun getStarredMemos(): Flow<List<Memo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemo(memo: Memo): Long

    @Update
    suspend fun updateMemo(memo: Memo)

    @Delete
    suspend fun deleteMemo(memo: Memo)

    @Query("DELETE FROM memos WHERE id = :id")
    suspend fun deleteMemoById(id: Long)

    @Query("UPDATE memos SET isStarred = :isStarred WHERE id = :id")
    suspend fun updateStarred(id: Long, isStarred: Boolean)

    @Query("SELECT * FROM memos WHERE id = :id")
    suspend fun getMemoById(id: Long): Memo?

    @Query("UPDATE memos SET tag = :newTag WHERE tag = :oldTag")
    suspend fun updateTagByTag(oldTag: String, newTag: String): Int

    @Query("DELETE FROM memos WHERE tag = :tag")
    suspend fun deleteByTag(tag: String)

    @Query("SELECT COUNT(*) FROM memos")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM memos WHERE isStarred = 1")
    fun getStarredCount(): Flow<Int>
}
