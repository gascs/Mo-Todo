package com.motut.mo.ui.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.motut.mo.data.Todo
import com.motut.mo.data.brandColor
import com.motut.mo.util.DateFormats

/**
 * 任务详情 BottomSheet - 展示任务完整信息和操作按钮
 * 使用统一的 Priority 扩展替代硬编码优先级颜色映射（P0-3）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailBottomSheet(
    todo: Todo,
    onDismiss: () -> Unit,
    onToggleComplete: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    // 使用统一优先级映射
    val priorityText = todo.priority.displayName
    val priorityColor = todo.priority.brandColor

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 标题栏
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(text = "任务详情", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "关闭") }
            }

            // 优先级标签
            Surface(color = priorityColor.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
                Text(text = priorityText, style = MaterialTheme.typography.labelSmall, color = priorityColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
            }

            // 标题和内容
            Text(text = todo.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (todo.content.isNotBlank()) {
                Text(text = todo.content, style = MaterialTheme.typography.bodyMedium)
            }

            // 日期和时间信息
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                todo.date?.let {
                    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(8.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(18.dp))
                            Text(text = it.format(DateFormats.isoDate), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
                todo.time?.let {
                    Surface(color = MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(8.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(18.dp))
                            Text(text = it.format(DateFormats.time24h), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // 操作按钮
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onToggleComplete(todo.id); onDismiss() },
                    modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.large) {
                    Icon(if (todo.isCompleted) Icons.Default.RadioButtonUnchecked else Icons.Default.CheckCircle, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (todo.isCompleted) "标记未完成" else "标记完成")
                }
                Button(onClick = { onDelete(todo.id) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                    shape = MaterialTheme.shapes.large) {
                    Icon(Icons.Default.Delete, null); Spacer(Modifier.width(8.dp)); Text("删除")
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
