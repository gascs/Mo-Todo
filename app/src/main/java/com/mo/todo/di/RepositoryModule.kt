package com.mo.todo.di

import com.mo.todo.data.dao.MemoDao
import com.mo.todo.data.dao.TodoDao
import com.mo.todo.repository.MemoRepository
import com.mo.todo.repository.TodoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTodoRepository(todoDao: TodoDao): TodoRepository {
        return TodoRepository(todoDao)
    }

    @Provides
    @Singleton
    fun provideMemoRepository(memoDao: MemoDao): MemoRepository {
        return MemoRepository(memoDao)
    }
}
