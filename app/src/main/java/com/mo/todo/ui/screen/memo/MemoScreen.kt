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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import compose.icons.Octicons
import compose.icons.octicons.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.R
import com.mo.todo.data.model.Memo
import com.mo.todo.data.model.TagConfig
import com.mo.todo.ui.component.MemoGridCard
import com.mo.todo.ui.component.MemoListItem
import com.mo.todo.ui.component.SectionHeader
import com.mo.todo.ui.component.TagChipRow
import com.mo.todo.ui.viewmodel.MemoViewModel
import com.mo.todo.ui.viewmodel.SettingsViewModel

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
            Column {
                Row(
                    Modifier.fillMaxWidth().padding(start = 24.dp, end = 8.dp, top = 24.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.memo_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(36.dp).clip(CircleShape).clickable { showOnlyStarred = !showOnlyStarred }.padding(6.dp)) {
                            Icon(
                                if (showOnlyStarred) Octicons.StarFill16 else Octicons.Star16,
                                stringResource(R.string.memo_filter_star),
                                tint = if (showOnlyStarred) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(Modifier.size(36.dp).clip(CircleShape).clickable { viewMode = if (viewMode == "grid") "list" else "grid" }.padding(6.dp)) {
                            Icon(
                                if (viewMode == "grid") Octicons.ListUnordered24 else Octicons.Note24,
                                stringResource(R.string.memo_toggle_view)
                            )
                        }
                    }
                }
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }
        },
        floatingActionButton = {
            Box(modifier = Modifier.padding(bottom = 80.dp)) {
                FloatingActionButton(
                    onClick = { onNavigateToAddEdit(null) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape
                ) {
                    Icon(Octicons.Plus24, stringResource(R.string.memo_btn_add))
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
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
                EmptyMemoState()
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
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    androidx.compose.material3.OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        placeholder = { Text(stringResource(R.string.memo_search_placeholder)) },
        leadingIcon = { Icon(Octicons.Search16, null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Box(Modifier.clip(CircleShape).clickable { onQueryChange("") }.padding(4.dp)) {
                    Icon(Octicons.XCircle16, stringResource(R.string.search_clear))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )
}

@Composable
private fun EmptyMemoState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Octicons.Note24,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.memo_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
