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

    /**
     * 导出备忘录为CSV格式
     */
    fun exportMemosToCsv(memos: List<Memo>, uri: Uri): Result<Unit> {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream, Charsets.UTF_8)).use { writer ->
                    // CSV表头
                    writer.write("ID,标题,内容,分类ID,是否置顶,创建时间,更新时间")
                    writer.newLine()
                    
                    // 写入数据
                    memos.forEach { memo ->
                        writer.write(buildCsvLine(
                            memo.id.toString(),
                            memo.title,
                            memo.content,
                            memo.categoryId?.toString() ?: "",
                            if (memo.isPinned) "是" else "否",
                            memo.createdAt.toString(),
                            memo.updatedAt.toString()
                        ))
                        writer.newLine()
                    }
                }
            } ?: throw Exception("无法打开输出流")
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 导出任其为CSV格式
     */
    fun exportTodosToCsv(todos: List<Todo>, uri: Uri): Result<Unit> {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                BufferedWriter(OutputStreamWriter(outputStream, Charsets.UTF_8)).use { writer ->
                    // CSV表头
                    writer.write("ID,标题,内容,地点,日期,时间,优先级,是否完成,创建时间,更新时间")
                    writer.newLine()
                    
                    // 写入数据
                    todos.forEach { todo ->
                        writer.write(buildCsvLine(
                            todo.id.toString(),
                            todo.title,
                            todo.content,
                            todo.location,
                            todo.date?.toString() ?: "",
                            todo.time?.toString() ?: "",
                            getPriorityText(todo.priority),
                            if (todo.isCompleted) "是" else "否",
                            todo.createdAt.toString(),
                            todo.updatedAt.toString()
                        ))
                        writer.newLine()
                    }
                }
            } ?: throw Exception("无法打开输出流")
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取统计数据摘要
     */
    fun getStatistics(memos: List<Memo>, todos: List<Todo>): String {
        val completedTodos = todos.count { it.isCompleted }
        val highPriorityTodos = todos.count { it.priority == Priority.HIGH && !it.isCompleted }
        val todayTodos = todos.count { it.date == LocalDate.now() && !it.isCompleted }
        val overdueTodos = todos.count { 
            it.date?.isBefore(LocalDate.now()) == true && !it.isCompleted 
        }
        val pinnedMemos = memos.count { it.isPinned }
        
        return buildString {
            appendLine("📊 Mo数据统计")
            appendLine("================")
            appendLine()
            appendLine("📝 备忘录:")
            appendLine("  - 总数: ${memos.size}")
            appendLine("  - 置顶: $pinnedMemos")
            appendLine()
            appendLine("✅ 待办事项:")
            appendLine("  - 总数: ${todos.size}")
            appendLine("  - 已完成: $completedTodos")
            appendLine("  - 未完成: ${todos.size - completedTodos}")
            appendLine("  - 今日待办: $todayTodos")
            appendLine("  - 已过期: $overdueTodos")
            appendLine("  - 高优先级: $highPriorityTodos")
            appendLine()
            appendLine("📅 导出时间: ${LocalDateTime.now()}")
        }
    }

    private fun getPriorityText(priority: Priority): String {
        return when (priority) {
            Priority.HIGH -> "高"
            Priority.MEDIUM -> "中"
            Priority.LOW -> "低"
        }
    }

    private fun buildCsvLine(vararg fields: String): String {
        return fields.joinToString(",") { field ->
            // 如果包含逗号、引号或换行符，需要用引号包裹并转义内部引号
            if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
                "\"${field.replace("\"", "\"\"")}\""
            } else {
                field
            }
        }
    }
}
