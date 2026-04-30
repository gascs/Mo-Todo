package com.mo.todo.ui.screen.memo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import compose.icons.Octicons
import compose.icons.octicons.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.data.model.Memo
import com.mo.todo.data.model.TagConfig
import com.mo.todo.ui.component.MemoGridCard
import com.mo.todo.ui.component.MemoListItem
import com.mo.todo.ui.component.TagChipRow
import com.mo.todo.ui.viewmodel.MemoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoScreen(
    onNavigateToAddEdit: (Long?) -> Unit,
    viewModel: MemoViewModel = hiltViewModel()
) {
    val memos by viewModel.memos.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val isGridView by viewModel.isGridView.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var contextMemo by remember { mutableStateOf<Memo?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "备忘",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.setSearchActive(!isSearchActive) }) {
                        Icon(
                            Octicons.Search24,
                            contentDescription = "搜索",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { viewModel.toggleViewMode() }) {
                        Icon(
                            if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Filled.GridView,
                            contentDescription = if (isGridView) "列表视图" else "网格视图",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Octicons.KebabHorizontal24,
                                contentDescription = "更多",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(text = { Text("按更新时间排序") }, onClick = { showMenu = false })
                            DropdownMenuItem(text = { Text("按星标筛选") }, onClick = { showMenu = false })
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            Box(modifier = Modifier.padding(bottom = 80.dp)) {
                FloatingActionButton(
                    onClick = { onNavigateToAddEdit(null) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(Octicons.Plus24, contentDescription = "新建备忘录")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            AnimatedVisibility(visible = isSearchActive, enter = fadeIn(), exit = fadeOut()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("搜索备忘录...", style = MaterialTheme.typography.bodyMedium) },
                    singleLine = true,
                    trailingIcon = {
                        TextButton(onClick = { viewModel.setSearchActive(false) }) {
                            Text("取消", style = MaterialTheme.typography.labelLarge)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = MaterialTheme.shapes.medium
                )
            }

            TagChipRow(
                items = TagConfig.memoTags,
                selectedKey = selectedTag,
                keySelector = { it.key },
                labelSelector = { it.label },
                onItemClick = { viewModel.setSelectedTag(it) }
            )

            if (memos.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.padding(bottom = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Octicons.Note24,
                                contentDescription = null,
                                modifier = Modifier.height(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                            )
                        }
                        Text(
                            "暂无备忘录",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "点击 + 开始记录",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Crossfade(targetState = isGridView, label = "viewTransition") { grid ->
                    if (grid) {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val columns = when {
                                maxWidth < 600.dp -> 2
                                maxWidth < 900.dp -> 3
                                else -> 4
                            }
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(columns),
                                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 92.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(memos, key = { it.id }) { memo ->
                                    MemoGridCard(
                                        memo = memo,
                                        onClick = { onNavigateToAddEdit(memo.id) },
                                        onToggleStar = { viewModel.toggleStarred(memo.id, !memo.isStarred) },
                                        onLongClick = { contextMemo = memo }
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 92.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(memos, key = { it.id }) { memo ->
                                MemoListItem(
                                    memo = memo,
                                    onClick = { onNavigateToAddEdit(memo.id) },
                                    onToggleStar = { viewModel.toggleStarred(memo.id, !memo.isStarred) },
                                    onLongClick = { contextMemo = memo }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    contextMemo?.let { memo ->
        AlertDialog(
            onDismissRequest = { contextMemo = null },
            title = { Text(memo.title, maxLines = 1, style = MaterialTheme.typography.titleMedium) },
            text = {
                Column {
                    TextButton(
                        onClick = { contextMemo = null; onNavigateToAddEdit(memo.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Octicons.Pencil24, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("编辑", modifier = Modifier.weight(1f))
                    }
                    TextButton(
                        onClick = { contextMemo = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Octicons.Copy24, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("复制文本", modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { contextMemo = null }) { Text("取消") }
            }
        )
    }
}
