package com.mo.todo.ui.screen.todo

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.data.model.Todo
import com.mo.todo.ui.theme.PriorityHigh
import com.mo.todo.ui.theme.PriorityLow
import com.mo.todo.ui.theme.PriorityMedium
import com.mo.todo.ui.theme.TagPersonal
import com.mo.todo.ui.theme.TagShopping
import com.mo.todo.ui.theme.TagWork
import com.mo.todo.ui.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TagItem(val key: String, val label: String)

val todoTags = listOf(
    TagItem("all", "全部"),
    TagItem("work", "工作"),
    TagItem("personal", "个人"),
    TagItem("shopping", "购物")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    onNavigateToAddEdit: (Long?) -> Unit,
    viewModel: TodoViewModel = hiltViewModel()
) {
    val activeTodos by viewModel.activeTodos.collectAsState()
    val completedTodos by viewModel.completedTodos.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showCompleted by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mo \u00b7 待办",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.setSearchActive(!isSearchActive) }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "搜索"
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "更多"
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("按优先级排序") },
                                onClick = { showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("按创建时间排序") },
                                onClick = { showMenu = false }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddEdit(null) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "新建待办"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isSearchActive) {
                androidx.compose.material3.OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("搜索待办...") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { viewModel.setSearchActive(false) }) {
                            Text("取消")
                        }
                    }
                )
            }

            // Tag chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(todoTags) { tag ->
                    FilterChip(
                        selected = selectedTag == tag.key,
                        onClick = { viewModel.setSelectedTag(tag.key) },
                        label = { Text(tag.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }

            // Active todos
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "待完成",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                if (activeTodos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "轻轻松松，没有待办",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(activeTodos, key = { it.id }) { todo ->
                    TodoItem(
                        todo = todo,
                        onToggleCompletion = {
                            viewModel.toggleCompletion(todo.id, !todo.isCompleted)
                        },
                        onClick = { onNavigateToAddEdit(todo.id) }
                    )
                }

                // Completed section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { showCompleted = !showCompleted },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = if (showCompleted) "收起已完成 (${completedTodos.size})" else "已完成 (${completedTodos.size})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (showCompleted) {
                    items(completedTodos, key = { it.id }) { todo ->
                        TodoItem(
                            todo = todo,
                            onToggleCompletion = {
                                viewModel.toggleCompletion(todo.id, !todo.isCompleted)
                            },
                            onClick = { onNavigateToAddEdit(todo.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    onToggleCompletion: () -> Unit,
    onClick: () -> Unit
) {
    val tagColor = when (todo.tag) {
        "work" -> TagWork
        "personal" -> TagPersonal
        "shopping" -> TagShopping
        else -> MaterialTheme.colorScheme.secondary
    }

    val priorityColor = when (todo.priority) {
        2 -> PriorityHigh
        1 -> PriorityMedium
        0 -> PriorityLow
        else -> PriorityMedium
    }

    val priorityLabel = when (todo.priority) {
        2 -> "高"
        1 -> "中"
        0 -> "低"
        else -> "中"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggleCompletion() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(priorityColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (todo.isCompleted)
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (todo.reminderTime != null) {
                        val dateFormat = remember { SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()) }
                        Text(
                            text = dateFormat.format(Date(todo.reminderTime)),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(tagColor)
                    )

                    Text(
                        text = priorityLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
