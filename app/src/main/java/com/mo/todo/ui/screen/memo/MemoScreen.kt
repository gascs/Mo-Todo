package com.mo.todo.ui.screen.memo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import compose.icons.Octicons
import compose.icons.octicons.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.R
import com.mo.todo.data.model.Memo
import com.mo.todo.data.model.TagConfig
import com.mo.todo.ui.component.MemoGridCard
import com.mo.todo.ui.component.MemoListItem
import com.mo.todo.ui.component.TagChipRow
import com.mo.todo.ui.viewmodel.MemoViewModel
import com.mo.todo.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoScreen(
    onNavigateToAddEdit: (Long?) -> Unit,
    memoViewModel: MemoViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val memos by memoViewModel.memos.collectAsState(initial = emptyList())
    val listDensity by settingsViewModel.listDensity.collectAsState()
    var selectedTag by remember { mutableStateOf("全部") }
    var viewMode by remember { mutableStateOf("grid") }
    var showOnlyStarred by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = memos.filter { m ->
        val tagMatch = selectedTag == "全部" || m.tag == selectedTag
        val starMatch = !showOnlyStarred || m.isStarred
        val searchMatch = searchQuery.isBlank() || m.title.contains(searchQuery, true) || m.content.contains(searchQuery, true)
        tagMatch && starMatch && searchMatch
    }

    val sorted = filtered.sortedByDescending { it.updatedAt }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.memo_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(
                            Octicons.Search24,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showOnlyStarred = !showOnlyStarred }) {
                        Icon(
                            if (showOnlyStarred) Octicons.StarFill16 else Octicons.Star16,
                            stringResource(R.string.memo_filter_star),
                            tint = if (showOnlyStarred) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { viewMode = if (viewMode == "grid") "list" else "grid" }) {
                        Icon(
                            if (viewMode == "grid") Octicons.ListUnordered24 else Octicons.Note24,
                            stringResource(R.string.memo_toggle_view),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                    Icon(Octicons.Plus24, contentDescription = stringResource(R.string.memo_btn_add))
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            AnimatedVisibility(visible = isSearchActive, enter = fadeIn(), exit = fadeOut()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text(stringResource(R.string.memo_search_placeholder), style = MaterialTheme.typography.bodyMedium) },
                    singleLine = true,
                    trailingIcon = {
                        TextButton(onClick = { isSearchActive = false; searchQuery = "" }) {
                            Text(stringResource(R.string.btn_cancel), style = MaterialTheme.typography.labelLarge)
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
                keySelector = { it.label },
                labelSelector = { tag ->
                    val resId = TagConfig.displayNameResId(tag.key)
                    if (resId != 0) context.getString(resId) else tag.label
                },
                onItemClick = { selectedTag = it }
            )

            if (sorted.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                            stringResource(R.string.memo_empty),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                if (viewMode == "grid") {
                    val cols = if (LocalConfiguration.current.screenWidthDp > 600) 3 else 2
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(cols),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        sorted.forEach { memo ->
                            item(key = "memo_${memo.id}") {
                                MemoGridCard(
                                    memo = memo,
                                    onClick = { onNavigateToAddEdit(memo.id) },
                                    onToggleStar = { memoViewModel.toggleStarred(memo.id, !memo.isStarred) },
                                    onLongClick = {
                                        memoViewModel.deleteMemo(memo)
                                    }
                                )
                            }
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        sorted.forEach { memo ->
                            item(key = "memo_${memo.id}") {
                                MemoListItem(
                                    memo = memo,
                                    onClick = { onNavigateToAddEdit(memo.id) },
                                    onToggleStar = { memoViewModel.toggleStarred(memo.id, !memo.isStarred) },
                                    onLongClick = {
                                        memoViewModel.deleteMemo(memo)
                                    }
                                )
                            }
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

