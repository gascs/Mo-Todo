package com.mo.todo.ui.screen.todo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.data.model.Todo
import com.mo.todo.ui.component.EmptyPlaceholder
import com.mo.todo.ui.component.SectionHeader
import com.mo.todo.ui.component.TagChipRow
import com.mo.todo.ui.component.TodoItemRow
import com.mo.todo.ui.viewmodel.TodoViewModel

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

    val isEmpty = activeTodos.isEmpty() && completedTodos.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mo · 待办",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
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
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddEdit(null) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.extraLarge,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "新建待办"
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(
                visible = isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("搜索待办...") },
                    singleLine = true,
                    trailingIcon = {
                        TextButton(onClick = { viewModel.setSearchActive(false) }) {
                            Text("取消")
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = MaterialTheme.shapes.small
                )
            }

            TagChipRow(
                items = todoTags,
                selectedKey = selectedTag,
                keySelector = { it.key },
                labelSelector = { it.label },
                onItemClick = { viewModel.setSelectedTag(it) }
            )

            if (isEmpty) {
                EmptyPlaceholder(
                    icon = Icons.Outlined.Checklist,
                    title = "轻轻松松，没有待办",
                    subtitle = "点击下方 + 开始添加吧"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        SectionHeader(title = "待完成")
                    }

                    items(activeTodos, key = { it.id }) { todo ->
                        TodoItemRow(
                            todo = todo,
                            onToggleCompletion = {
                                viewModel.toggleCompletion(todo.id, !todo.isCompleted)
                            },
                            onClick = { onNavigateToAddEdit(todo.id) },
                            onSwipeDelete = { viewModel.deleteTodo(todo) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(
                            onClick = { showCompleted = !showCompleted },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = if (showCompleted)
                                    "收起已完成 (${completedTodos.size})"
                                else
                                    "已完成 (${completedTodos.size})",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    item {
                        AnimatedVisibility(
                            visible = showCompleted,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Column(modifier = Modifier.animateContentSize()) {
                                completedTodos.forEach { todo ->
                                    TodoItemRow(
                                        todo = todo,
                                        onToggleCompletion = {
                                            viewModel.toggleCompletion(todo.id, !todo.isCompleted)
                                        },
                                        onClick = { onNavigateToAddEdit(todo.id) },
                                        onSwipeDelete = { viewModel.deleteTodo(todo) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
