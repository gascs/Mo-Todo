package com.motut.mo

import android.app.Application
import com.motut.mo.database.AppDatabase
import com.motut.mo.repository.MemoRepository
import com.motut.mo.repository.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MoApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    
    val database by lazy { AppDatabase.getDatabase(this) }
    val todoRepository by lazy { TodoRepository(database.todoDao()) }
    val memoRepository by lazy { MemoRepository(database.memoDao()) }
}
