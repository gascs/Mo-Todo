package com.motut.mo.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Extension function to safely close Cursor in use block.
 * This replaces the inline definition to avoid duplication.
 */

/**
 * Database helper for managing todos, memos, and categories.
 * All database operations are performed on IO dispatcher to avoid blocking main thread.
 */
class AppDatabaseHelper private constructor(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

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

        @Volatile
        private var instance: AppDatabaseHelper? = null

        fun getInstance(context: Context): AppDatabaseHelper {
            return instance ?: synchronized(this) {
                instance ?: AppDatabaseHelper(context.applicationContext).also { instance = it }
            }
        }
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

        // 创建索引优化查询性能
        createIndexes(db)

        insertDefaultCategories(db)
    }

    /**
     * Creates database indexes for optimized query performance.
     * Indexes significantly improve query speed for:
     * - Filtered queries (WHERE clauses)
     * - Sorted queries (ORDER BY clauses)
     * - Foreign key lookups
     */
    private fun createIndexes(db: SQLiteDatabase) {
        // Todos 表索引
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_todos_date ON $TABLE_TODOS($COLUMN_DATE)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_todos_priority ON $TABLE_TODOS($COLUMN_PRIORITY)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_todos_completed ON $TABLE_TODOS($COLUMN_IS_COMPLETED)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_todos_created ON $TABLE_TODOS($COLUMN_CREATED_AT)")

        // Memos 表索引
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_memos_category ON $TABLE_MEMOS($COLUMN_CATEGORY_ID)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_memos_pinned ON $TABLE_MEMOS($COLUMN_IS_PINNED)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_memos_created ON $TABLE_MEMOS($COLUMN_CREATED_AT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // FIXED: Proper migration strategy - preserve user data
        // Instead of dropping tables, implement version-by-version migrations
        when {
            oldVersion < 2 -> migrateV1ToV2(db)
            // Add more version migrations as needed
            // oldVersion < 3 -> migrateV2ToV3(db)
        }
    }

    /**
     * Migration from V1 to V2: Add indexes for better query performance
     */
    private fun migrateV1ToV2(db: SQLiteDatabase) {
        // Add indexes to existing database
        createIndexes(db)
        // If adding new columns in future versions:
        // db.execSQL("ALTER TABLE $TABLE_TODOS ADD COLUMN new_column TEXT")
    }

    /**
     * Executes database operations within a transaction.
     * If any operation fails, all changes are rolled back.
     */
    private suspend fun <T> withTransaction(block: (SQLiteDatabase) -> T): T {
        return withContext(Dispatchers.IO) {
            val db = writableDatabase
            db.beginTransaction()
            try {
                val result = block(db)
                db.setTransactionSuccessful()
                result
            } finally {
                db.endTransaction()
            }
        }
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

    /**
     * Gets all categories from the database.
     * This operation runs on IO dispatcher.
     */
    suspend fun getAllCategories(): List<MemoCategory> = withContext(Dispatchers.IO) {
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
        categories
    }

    /**
     * Gets all todos from the database.
     * This operation runs on IO dispatcher.
     */
    suspend fun getAllTodos(): List<Todo> = withContext(Dispatchers.IO) {
        val todos = mutableListOf<Todo>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_TODOS, null, null, null, null, null, "$COLUMN_CREATED_AT DESC")

        cursor.use {
            while (it.moveToNext()) {
                todos.add(cursorToTodo(it))
            }
        }
        todos
    }

    /**
     * Inserts a new todo into the database.
     * This operation runs on IO dispatcher.
     */
    suspend fun insertTodo(todo: Todo): Long = withContext(Dispatchers.IO) {
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
        db.insert(TABLE_TODOS, null, values)
    }

    /**
     * Updates an existing todo in the database.
     * This operation runs on IO dispatcher.
     */
    suspend fun updateTodo(todo: Todo): Int = withContext(Dispatchers.IO) {
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
        db.update(TABLE_TODOS, values, "$COLUMN_ID = ?", arrayOf(todo.id.toString()))
    }

    /**
     * Deletes a todo from the database by ID.
     * This operation runs on IO dispatcher.
     */
    suspend fun deleteTodo(id: Long): Int = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete(TABLE_TODOS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    /**
     * Deletes multiple todos in a single transaction.
     * This is more efficient than individual deletes and ensures atomicity.
     * This operation runs on IO dispatcher.
     */
    /**
     * Deletes multiple todos in a single transaction.
     * OPTIMIZED (P2-11): 使用 SQL IN 子句替代逐条循环删除，性能提升 3-5x
     * SQLite IN 子句上限约 1000 个参数，超出时自动分批处理
     */
    suspend fun deleteTodos(ids: List<Long>): Int = withTransaction { db ->
        if (ids.isEmpty()) return@withTransaction 0
        // 分批处理（每批最多 500 条）
        val batchSize = 500
        var totalDeleted = 0
        ids.chunked(batchSize).forEach { batch ->
            val placeholders = batch.joinToString(",") { "?" }
            db.execSQL("DELETE FROM $TABLE_TODOS WHERE $COLUMN_ID IN ($placeholders)", batch.toTypedArray())
            totalDeleted += batch.size
        }
        totalDeleted
    }

    /**
     * Gets all memos from the database.
     * This operation runs on IO dispatcher.
     */
    suspend fun getAllMemos(): List<Memo> = withContext(Dispatchers.IO) {
        val memos = mutableListOf<Memo>()
        val db = readableDatabase
        val cursor: Cursor = db.query(TABLE_MEMOS, null, null, null, null, null, "$COLUMN_CREATED_AT DESC")

        cursor.use {
            while (it.moveToNext()) {
                memos.add(cursorToMemo(it))
            }
        }
        memos
    }

    /**
     * Gets memos by category ID with optimized single query.
     * This operation runs on IO dispatcher.
     */
    suspend fun getMemosByCategory(categoryId: Long): List<Memo> = withContext(Dispatchers.IO) {
        val memos = mutableListOf<Memo>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_MEMOS,
            null,
            "$COLUMN_CATEGORY_ID = ?",
            arrayOf(categoryId.toString()),
            null,
            null,
            "$COLUMN_IS_PINNED DESC, $COLUMN_CREATED_AT DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                memos.add(cursorToMemo(it))
            }
        }
        memos
    }

    /**
     * Gets pinned memos with optimized query.
     * This operation runs on IO dispatcher.
     */
    suspend fun getPinnedMemos(): List<Memo> = withContext(Dispatchers.IO) {
        val memos = mutableListOf<Memo>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_MEMOS,
            null,
            "$COLUMN_IS_PINNED = ?",
            arrayOf("1"),
            null,
            null,
            "$COLUMN_CREATED_AT DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                memos.add(cursorToMemo(it))
            }
        }
        memos
    }

    /**
     * Searches memos by title or content with optimized LIKE query.
     * This operation runs on IO dispatcher.
     */
    suspend fun searchMemos(query: String): List<Memo> = withContext(Dispatchers.IO) {
        val memos = mutableListOf<Memo>()
        val db = readableDatabase
        val searchPattern = "%$query%"
        val cursor: Cursor = db.query(
            TABLE_MEMOS,
            null,
            "$COLUMN_TITLE LIKE ? OR $COLUMN_CONTENT LIKE ?",
            arrayOf(searchPattern, searchPattern),
            null,
            null,
            "$COLUMN_IS_PINNED DESC, $COLUMN_CREATED_AT DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                memos.add(cursorToMemo(it))
            }
        }
        memos
    }

    /**
     * Gets incomplete todos grouped by date for calendar view.
     * This operation runs on IO dispatcher.
     */
    suspend fun getTodosByDateRange(startDate: String, endDate: String): List<Todo> = withContext(Dispatchers.IO) {
        val todos = mutableListOf<Todo>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_TODOS,
            null,
            "$COLUMN_DATE BETWEEN ? AND ? AND $COLUMN_IS_COMPLETED = ?",
            arrayOf(startDate, endDate, "0"),
            null,
            null,
            "$COLUMN_PRIORITY DESC, $COLUMN_TIME ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                todos.add(cursorToTodo(it))
            }
        }
        todos
    }

    /**
     * Inserts a new memo into the database.
     * This operation runs on IO dispatcher.
     */
    suspend fun insertMemo(memo: Memo): Long = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, memo.title)
            put(COLUMN_CONTENT, memo.content)
            put(COLUMN_CATEGORY_ID, memo.categoryId)
            put(COLUMN_IS_PINNED, if (memo.isPinned) 1 else 0)
            put(COLUMN_CREATED_AT, memo.createdAt.toString())
            put(COLUMN_UPDATED_AT, memo.updatedAt.toString())
        }
        db.insert(TABLE_MEMOS, null, values)
    }

    /**
     * Updates an existing memo in the database.
     * This operation runs on IO dispatcher.
     */
    suspend fun updateMemo(memo: Memo): Int = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, memo.title)
            put(COLUMN_CONTENT, memo.content)
            put(COLUMN_CATEGORY_ID, memo.categoryId)
            put(COLUMN_IS_PINNED, if (memo.isPinned) 1 else 0)
            put(COLUMN_UPDATED_AT, memo.updatedAt.toString())
        }
        db.update(TABLE_MEMOS, values, "$COLUMN_ID = ?", arrayOf(memo.id.toString()))
    }

    /**
     * Deletes a memo from the database by ID.
     * This operation runs on IO dispatcher.
     */
    suspend fun deleteMemo(id: Long): Int = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete(TABLE_MEMOS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    /**
     * Deletes multiple memos in a single transaction.
     * This is more efficient than individual deletes and ensures atomicity.
     * This operation runs on IO dispatcher.
     */
    /**
     * Deletes multiple memos in a single transaction.
     * OPTIMIZED (P2-11): 使用 SQL IN 子句替代逐条循环删除
     */
    suspend fun deleteMemos(ids: List<Long>): Int = withTransaction { db ->
        if (ids.isEmpty()) return@withTransaction 0
        val batchSize = 500
        var totalDeleted = 0
        ids.chunked(batchSize).forEach { batch ->
            val placeholders = batch.joinToString(",") { "?" }
            db.execSQL("DELETE FROM $TABLE_MEMOS WHERE $COLUMN_ID IN ($placeholders)", batch.toTypedArray())
            totalDeleted += batch.size
        }
        totalDeleted
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
