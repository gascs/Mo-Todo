package com.motut.mo.data

import android.content.Context
import android.net.Uri
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Serializable
data class BackupData(
    val version: Int = 1,
    val exportTime: String,
    val memos: List<MemoBackup>,
    val todos: List<TodoBackup>
)

@Serializable
data class MemoBackup(
    val id: Long,
    val title: String,
    val content: String,
    val categoryId: Long? = null,
    val isPinned: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class TodoBackup(
    val id: Long,
    val title: String,
    val content: String = "",
    val location: String = "",
    val date: String? = null,
    val time: String? = null,
    val priority: String,
    val isCompleted: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

class DataBackupManager(private val context: Context) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun exportData(
        memos: List<Memo>,
        todos: List<Todo>,
        uri: Uri
    ): Result<Unit> {
        return try {
            val memoBackups = memos.map { memo ->
                MemoBackup(
                    id = memo.id,
                    title = memo.title,
                    content = memo.content,
                    categoryId = memo.categoryId,
                    isPinned = memo.isPinned,
                    createdAt = memo.createdAt.toString(),
                    updatedAt = memo.updatedAt.toString()
                )
            }

            val todoBackups = todos.map { todo ->
                TodoBackup(
                    id = todo.id,
                    title = todo.title,
                    content = todo.content,
                    location = todo.location,
                    date = todo.date?.toString(),
                    time = todo.time?.toString(),
                    priority = todo.priority.name,
                    isCompleted = todo.isCompleted,
                    createdAt = todo.createdAt.toString(),
                    updatedAt = todo.updatedAt.toString()
                )
            }

            val backupData = BackupData(
                exportTime = LocalDateTime.now().toString(),
                memos = memoBackups,
                todos = todoBackups
            )

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                    writer.write(json.encodeToString(backupData))
                }
            } ?: throw Exception("无法打开输出流")

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun importData(uri: Uri): Result<BackupData> {
        return try {
            val content = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            } ?: throw Exception("无法打开输入流")

            val backupData = json.decodeFromString<BackupData>(content)
            Result.success(backupData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun convertMemoBackup(backup: MemoBackup): Memo {
        return Memo(
            id = backup.id,
            title = backup.title,
            content = backup.content,
            categoryId = backup.categoryId,
            isPinned = backup.isPinned,
            createdAt = LocalDateTime.parse(backup.createdAt),
            updatedAt = LocalDateTime.parse(backup.updatedAt)
        )
    }

    fun convertTodoBackup(backup: TodoBackup): Todo {
        return Todo(
            id = backup.id,
            title = backup.title,
            content = backup.content,
            location = backup.location,
            date = backup.date?.let { LocalDate.parse(it) },
            time = backup.time?.let { LocalTime.parse(it) },
            priority = Priority.valueOf(backup.priority),
            isCompleted = backup.isCompleted,
            createdAt = LocalDateTime.parse(backup.createdAt),
            updatedAt = LocalDateTime.parse(backup.updatedAt)
        )
    }
}
