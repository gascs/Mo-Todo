package com.mo.todo.di

import android.content.Context
import androidx.room.Room
import com.mo.todo.data.dao.MemoDao
import com.mo.todo.data.dao.TodoDao
import com.mo.todo.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mo_database"
        ).build()
    }

    @Provides
    fun provideTodoDao(database: AppDatabase): TodoDao {
        return database.todoDao()
    }

    @Provides
    fun provideMemoDao(database: AppDatabase): MemoDao {
        return database.memoDao()
    }
}
