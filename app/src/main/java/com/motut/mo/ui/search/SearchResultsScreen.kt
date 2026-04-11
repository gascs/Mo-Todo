package com.motut.mo.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.motut.mo.data.Memo
import com.motut.mo.data.Todo
import com.motut.mo.ui.components.EmptyState

/**
 * 搜索结果页面 - 展示匹配的任务和备忘录，支持关键词高亮
 */
@Composable
fun SearchResultsScreen(
    searchQuery: String,
    matchedTodos: List<Todo>,
    matchedMemos: List<Memo>,
    onTodoClick: (Todo) -> Unit,
    onMemoClick: (Memo) -> Unit
) {
    val colors = listOf(
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer,
        MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    )

    val totalResults = matchedTodos.size + matchedMemos.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (searchQuery.isBlank()) {
            EmptyState(Icons.Default.Search, "搜索你的任务和备忘录", "输入关键词开始搜索")
        } else if (matchedTodos.isEmpty() && matchedMemos.isEmpty()) {
            EmptyState(Icons.Default.SearchOff, "未找到结果", "尝试使用其他关键词搜索")
        } else {
            // 统计信息
            Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small) {
                Text(text = "找到 $totalResults 个结果",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }

            if (matchedTodos.isNotEmpty()) {
                Surface(color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Checklist, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Text(text = "任务", style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Surface(color = MaterialTheme.colorScheme.primary, shape = CircleShape) {
                                Text(text = "${matchedTodos.size}", style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        matchedTodos.take(10).forEach { todo ->
                            SearchResultTodoItem(todo, searchQuery, onClick = { onTodoClick(todo) })
                        }
                        if (matchedTodos.size > 10) {
                            Text(text = "还有 ${matchedTodos.size - 10} 个任务...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 28.dp))
                        }
                    }
                }
            }

            if (matchedMemos.isNotEmpty()) {
                Surface(color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f), shape = MaterialTheme.shapes.medium) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.AutoMirrored.Default.Note, null,
                                tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                            Text(text = "备忘录", style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            Surface(color = MaterialTheme.colorScheme.secondary, shape = CircleShape) {
                                Text(text = "${matchedMemos.size}", style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        matchedMemos.take(10).forEachIndexed { index, memo ->
                            val (containerColor, contentColor) = colors[index % 4]
                            SearchResultMemoItem(memo, searchQuery, containerColor, contentColor, onClick = { onMemoClick(memo) })
                        }
                        if (matchedMemos.size > 10) {
                            Text(text = "还有 ${matchedMemos.size - 10} 条备忘录...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 28.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SearchResultTodoItem(todo: Todo, searchQuery: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Checkbox(checked = todo.isCompleted, onCheckedChange = null, enabled = false,
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary))
            Column(modifier = Modifier.weight(1f)) {
                HighlightedText(todo.title, searchQuery,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    highlightColor = MaterialTheme.colorScheme.primaryContainer)
                if (todo.content.isNotBlank()) {
                    HighlightedText(todo.content, searchQuery, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        highlightColor = MaterialTheme.colorScheme.tertiaryContainer, maxLines = 1)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun SearchResultMemoItem(memo: Memo, searchQuery: String, containerColor: Color, contentColor: Color, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(color = containerColor, shape = CircleShape, modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Default.Note, null, tint = contentColor, modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                HighlightedText(memo.title, searchQuery,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    highlightColor = MaterialTheme.colorScheme.primaryContainer)
                if (memo.content.isNotBlank()) {
                    HighlightedText(memo.content, searchQuery, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        highlightColor = MaterialTheme.colorScheme.tertiaryContainer, maxLines = 1)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun HighlightedText(
    text: String,
    highlight: String,
    style: TextStyle,
    color: Color = MaterialTheme.colorScheme.onSurface,
    highlightColor: Color = MaterialTheme.colorScheme.primaryContainer,
    maxLines: Int = Int.MAX_VALUE
) {
    if (highlight.isBlank()) {
        Text(text = text, style = style, color = color, maxLines = maxLines, overflow = TextOverflow.Ellipsis)
        return
    }

    val annotatedString = remember(text, highlight) {
        buildAnnotatedString {
            var currentIndex = 0
            val lowerText = text.lowercase()
            val lowerHighlight = highlight.lowercase()

            while (currentIndex < text.length) {
                val matchIndex = lowerText.indexOf(lowerHighlight, currentIndex)
                if (matchIndex == -1) {
                    append(text.substring(currentIndex))
                    break
                }
                if (matchIndex > currentIndex) { append(text.substring(currentIndex, matchIndex)) }
                pushStyle(SpanStyle(background = highlightColor, fontWeight = FontWeight.Bold))
                append(text.substring(matchIndex, matchIndex + highlight.length))
                pop()
                currentIndex = matchIndex + highlight.length
            }
        }
    }

    Text(text = annotatedString, style = style, maxLines = maxLines, overflow = TextOverflow.Ellipsis)
}
