package com.motut.mo.ui.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.motut.mo.data.Memo
import com.motut.mo.ui.components.EmptyState
import com.motut.mo.ui.tasks.TasksScreen
import com.motut.mo.util.DateFormats
import kotlinx.coroutines.delay

/**
 * 备忘录列表页面 - 支持筛选（全部/已置顶）和视图切换（网格/列表）
 */
@Composable
fun NotesScreen(
    memos: List<Memo>,
    viewModel: com.motut.mo.viewmodel.AppViewModel,
    onMemoClick: (Memo) -> Unit,
    onAddNoteClick: () -> Unit
) {
    val colors = listOf(
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer,
        MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    )

    var isGridView by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("全部") }
    val filters = listOf("全部", "已置顶")

    val filteredMemos by remember(memos, selectedFilter) {
        derivedStateOf {
            if (selectedFilter == "已置顶") memos.filter { it.isPinned } else memos
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }

            IconToggleButton(checked = isGridView, onCheckedChange = { isGridView = it }) {
                Icon(
                    imageVector = if (isGridView) Icons.Default.GridView else Icons.Default.ViewAgenda,
                    contentDescription = if (isGridView) "网格视图" else "列表视图"
                )
            }
        }

        if (filteredMemos.isEmpty()) {
            EmptyState(
                icon = Icons.Default.EditNote,
                title = "暂无备忘录",
                description = "记录你的想法和灵感",
                actionText = "新建备忘录",
                onActionClick = onAddNoteClick
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredMemos, key = { it.id }) { memo ->
                    val (containerColor, contentColor) = colors[memo.id.toInt() % 4]
                    NoteItemCard(
                        memo = memo,
                        containerColor = containerColor,
                        contentColor = contentColor,
                        onClick = { onMemoClick(memo) },
                        onTogglePin = { viewModel.toggleMemoPin(memo.id) },
                        onDelete = { viewModel.deleteMemo(memo.id) }
                    )
                }
            }
        }
    }
}
