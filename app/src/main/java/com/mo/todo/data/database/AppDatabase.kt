package com.mo.todo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mo.todo.data.dao.MemoDao
import com.mo.todo.data.dao.TodoDao
import com.mo.todo.data.model.Memo
import com.mo.todo.data.model.Todo

@Database(
    entities = [Todo::class, Memo::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun memoDao(): MemoDao
}
