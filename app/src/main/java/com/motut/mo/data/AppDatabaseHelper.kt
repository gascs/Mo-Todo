package com.motut.mo.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

inline fun <T> Cursor.use(block: (Cursor) -> T): T {
    try {
        return block(this)
    } finally {
        close()
    }
}

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mo_database.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_TODOS = "todos"
        private const val TABLE_MEMOS = "memos"
        private const val TABLE_CATEGORIES = "categories"

        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_LOCATION = "location"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_PRIORITY = "priority"
        private const val COLUMN_IS_COMPLETED = "is_completed"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_UPDATED_AT = "updated_at"
        private const val COLUMN_CATEGORY_ID = "category_id"
        private const val COLUMN_IS_PINNED = "is_pinned"
        private const val COLUMN_COLOR = "color"
        private const val COLUMN_NAME = "name"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_CATEGORIES (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_COLOR INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_TODOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_CONTENT TEXT,
                $COLUMN_LOCATION TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_TIME TEXT,
                $COLUMN_PRIORITY TEXT NOT NULL,
                $COLUMN_IS_COMPLETED INTEGER NOT NULL DEFAULT 0,
                $COLUMN_CREATED_AT TEXT NOT NULL,
                $COLUMN_UPDATED_AT TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE $TABLE_MEMOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_CONTENT TEXT NOT NULL,
                $COLUMN_CATEGORY_ID INTEGER,
                $COLUMN_IS_PINNED INTEGER NOT NULL DEFAULT 0,
                $COLUMN_CREATED_AT TEXT NOT NULL,
                $COLUMN_UPDATED_AT TEXT NOT NULL,
                FOREIGN KEY ($COLUMN_CATEGORY_ID) REFERENCES $TABLE_CATEGORIES($COLUMN_ID)
            )
        """.trimIndent())

        insertDefaultCategories(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEMOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TODOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        onCreate(db)
    }

    private fun insertDefaultCategories(db: SQLiteDatabase) {
        val categories = listOf(
            MemoCategory(1L, "工作", 0xFF4CAF50),
            MemoCategory(2L, "个人", 0xFF2196F3),
            MemoCategory(3L, "学习", 0xFFFF9800),
            MemoCategory(4L, "其他", 0xFF9E9E9E)
        )

        categories.forEach { category ->
            val values = ContentValues().apply {
                put(COLUMN_ID, category.id)
                put(COLUMN_NAME, category.name)
                put(COLUMN_COLOR, category.color)
            }
            db.insert(TABLE_CATEGORIES, null, values)
        }
    }

    fun getAllCategories(): List<MemoCategory> {
        val categories = mutableListOf<MemoCategory>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, null)
        
        cursor.use {
            while (it.moveToNext()) {
                categories.add(
                    MemoCategory(
                        id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                        color = it.getLong(it.getColumnIndexOrThrow(COLUMN_COLOR))
                    )
                )
            }
        }
        return categories
    }

    fun getAllTodos(): List<Todo> {
        val todos = mutableListOf<Todo>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_TODOS, null, null, null, null, null, "$COLUMN_CREATED_AT DESC")
        
        cursor.use {
            while (it.moveToNext()) {
                todos.add(cursorToTodo(it))
            }
        }
        return todos
    }

    fun insertTodo(todo: Todo): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, todo.title)
            put(COLUMN_CONTENT, todo.content)
            put(COLUMN_LOCATION, todo.location)
            put(COLUMN_DATE, todo.date?.toString())
            put(COLUMN_TIME, todo.time?.toString())
            put(COLUMN_PRIORITY, todo.priority.name)
            put(COLUMN_IS_COMPLETED, if (todo.isCompleted) 1 else 0)
            put(COLUMN_CREATED_AT, todo.createdAt.toString())
            put(COLUMN_UPDATED_AT, todo.updatedAt.toString())
        }
        return db.insert(TABLE_TODOS, null, values)
    }

    fun updateTodo(todo: Todo): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, todo.title)
            put(COLUMN_CONTENT, todo.content)
            put(COLUMN_LOCATION, todo.location)
            put(COLUMN_DATE, todo.date?.toString())
            put(COLUMN_TIME, todo.time?.toString())
            put(COLUMN_PRIORITY, todo.priority.name)
            put(COLUMN_IS_COMPLETED, if (todo.isCompleted) 1 else 0)
            put(COLUMN_UPDATED_AT, todo.updatedAt.toString())
        }
        return db.update(TABLE_TODOS, values, "$COLUMN_ID = ?", arrayOf(todo.id.toString()))
    }

    fun deleteTodo(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_TODOS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun getAllMemos(): List<Memo> {
        val memos = mutableListOf<Memo>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_MEMOS, null, null, null, null, null, "$COLUMN_CREATED_AT DESC")
        
        cursor.use {
            while (it.moveToNext()) {
                memos.add(cursorToMemo(it))
            }
        }
        return memos
    }

    fun insertMemo(memo: Memo): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, memo.title)
            put(COLUMN_CONTENT, memo.content)
            put(COLUMN_CATEGORY_ID, memo.categoryId)
            put(COLUMN_IS_PINNED, if (memo.isPinned) 1 else 0)
            put(COLUMN_CREATED_AT, memo.createdAt.toString())
            put(COLUMN_UPDATED_AT, memo.updatedAt.toString())
        }
        return db.insert(TABLE_MEMOS, null, values)
    }

    fun updateMemo(memo: Memo): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, memo.title)
            put(COLUMN_CONTENT, memo.content)
            put(COLUMN_CATEGORY_ID, memo.categoryId)
            put(COLUMN_IS_PINNED, if (memo.isPinned) 1 else 0)
            put(COLUMN_UPDATED_AT, memo.updatedAt.toString())
        }
        return db.update(TABLE_MEMOS, values, "$COLUMN_ID = ?", arrayOf(memo.id.toString()))
    }

    fun deleteMemo(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_MEMOS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    private fun cursorToTodo(cursor: Cursor): Todo {
        return Todo(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
            content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)) ?: "",
            location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)) ?: "",
            date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))?.let { LocalDate.parse(it) },
            time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))?.let { LocalTime.parse(it) },
            priority = Priority.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY))),
            isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1,
            createdAt = LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))),
            updatedAt = LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT)))
        )
    }

    private fun cursorToMemo(cursor: Cursor): Memo {
        return Memo(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
            content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
            categoryId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)).let { 
                if (it == 0L) null else it 
            },
            isPinned = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_PINNED)) == 1,
            createdAt = LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))),
            updatedAt = LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UPDATED_AT)))
        )
    }
}
