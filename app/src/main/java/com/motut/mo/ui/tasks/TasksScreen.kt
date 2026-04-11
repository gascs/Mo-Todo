package com.motut.mo.ui.tasks

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.motut.mo.data.Todo
import com.motut.mo.data.brandColor
import com.motut.mo.ui.components.EmptyState
import com.motut.mo.ui.theme.AppColors
import com.motut.mo.viewmodel.AppViewModel
import kotlinx.coroutines.delay

/**
 * 待办事项列表页面 - 展示所有待办任务，支持筛选（全部/今天/本周/已完成）
 */
@Composable
fun TasksScreen(
    todos: List<Todo>,
    viewModel: AppViewModel,
    onTodoClick: (Todo) -> Unit,
    onAddTaskClick: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("全部") }
    val filters = listOf("全部", "今天", "本周", "已完成")

    val pendingTodos by remember(todos, selectedFilter) {
        derivedStateOf {
            when (selectedFilter) {
                "已完成" -> todos.filter { it.isCompleted }
                else -> todos.filter { !it.isCompleted }
            }
        }
    }
    val completedTodos by remember(todos) {
        derivedStateOf { todos.filter { it.isCompleted } }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) }
                )
            }
        }

        if (pendingTodos.isEmpty() && completedTodos.isEmpty()) {
            EmptyState(
                icon = Icons.Default.CheckCircle,
                title = "暂无待办事项",
                description = "点击右下角按钮添加新任务",
                actionText = "新建任务",
                onActionClick = onAddTaskClick
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (pendingTodos.isNotEmpty()) {
                    item { TaskGroupHeader(title = "待完成", count = pendingTodos.size) }
                    items(pendingTodos, key = { it.id }) { todo ->
                        TaskItemCard(
                            todo = todo,
                            onClick = { onTodoClick(todo) },
                            onToggleComplete = { viewModel.toggleTodoCompletion(todo.id) },
                            onDelete = { viewModel.deleteTodo(todo.id) }
                        )
                    }
                }

                if (completedTodos.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    item { TaskGroupHeader(title = "已完成", count = completedTodos.size) }
                    items(completedTodos, key = { it.id }) { todo ->
                        TaskItemCard(
                            todo = todo,
                            onClick = { onTodoClick(todo) },
                            onToggleComplete = { viewModel.toggleTodoCompletion(todo.id) },
                            onDelete = { viewModel.deleteTodo(todo.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskGroupHeader(title: String, count: Int) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
