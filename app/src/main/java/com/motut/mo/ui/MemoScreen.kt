package com.motut.mo.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.motut.mo.data.Memo
import com.motut.mo.viewmodel.AppViewModel
import java.time.format.DateTimeFormatter

private val dateFormatter by lazy { DateTimeFormatter.ofPattern("MM-dd HH:mm") }

// 备忘录排序选项
enum class MemoSortOption(val title: String) {
    DEFAULT("默认（置顶优先）"),
    DATE_NEW("最新优先"),
    DATE_OLD("最早优先"),
    TITLE("标题排序")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoScreen(
    viewModel: AppViewModel = viewModel(),
    onMemoClick: (Memo) -> Unit
) {
    val memos by viewModel.memos.collectAsState()
    val categories by viewModel.categories.collectAsState()

    // 批量选择状态
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedMemoIds by remember { mutableStateOf(setOf<Long>()) }

    // 排序状态
    var sortOption by remember { mutableStateOf(MemoSortOption.DEFAULT) }
    var showSortMenu by remember { mutableStateOf(false) }

    // 下拉刷新状态
    var isRefreshing by remember { mutableStateOf(false) }

    // 刷新模拟
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            kotlinx.coroutines.delay(800)
            isRefreshing = false
        }
    }

    // 排序后的备忘录列表
    val sortedMemos by remember(memos, sortOption) {
        derivedStateOf {
            when (sortOption) {
                MemoSortOption.DEFAULT -> viewModel.getSortedMemos(memos)
                MemoSortOption.DATE_NEW -> memos.sortedByDescending { it.createdAt }
                MemoSortOption.DATE_OLD -> memos.sortedBy { it.createdAt }
                MemoSortOption.TITLE -> memos.sortedBy { it.title }
            }
        }
    }

    // 批量删除选中备忘录
    val deleteSelectedMemos = {
        selectedMemoIds.forEach { id ->
            viewModel.deleteMemo(id)
        }
        selectedMemoIds = emptySet()
        isSelectionMode = false
    }

    // 批量置顶/取消置顶
    val togglePinSelectedMemos = {
        selectedMemoIds.forEach { id ->
            viewModel.toggleMemoPin(id)
        }
        selectedMemoIds = emptySet()
        isSelectionMode = false
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 批量操作栏
        if (isSelectionMode) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(onClick = {
                            isSelectionMode = false
                            selectedMemoIds = emptySet()
                        }) {
                            Icon(Icons.Default.Close, "取消")
                        }
                        Text(
                            "已选择 ${selectedMemoIds.size} 项",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = togglePinSelectedMemos) {
                            Icon(Icons.Default.PushPin, "置顶")
                        }
                        IconButton(onClick = deleteSelectedMemos) {
                            Icon(Icons.Default.Delete, "删除", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        // 排序选项栏
        if (!isSelectionMode && memos.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "共 ${memos.size} 条备忘录",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box {
                    TextButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.Default.Sort, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(sortOption.title)
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        MemoSortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.title) },
                                onClick = {
                                    sortOption = option
                                    showSortMenu = false
                                },
                                leadingIcon = {
                                    if (sortOption == option) {
                                        Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        if (sortedMemos.isEmpty()) {
            EmptyMemoState()
        } else {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { isRefreshing = true },
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 置顶备忘录
                    val pinnedMemos = sortedMemos.filter { it.isPinned }
                    if (pinnedMemos.isNotEmpty()) {
                        item {
                            MemoSectionHeader(
                                title = "置顶",
                                count = pinnedMemos.size
                            )
                        }
                        items(pinnedMemos, key = { "pinned_${it.id}" }) { memo ->
                            SwipeableMemoItem(
                                memo = memo,
                                categories = categories,
                                isSelected = selectedMemoIds.contains(memo.id),
                                isSelectionMode = isSelectionMode,
                                onClick = { if (!isSelectionMode) onMemoClick(memo) },
                                onTogglePin = { viewModel.toggleMemoPin(memo.id) },
                                onDelete = { viewModel.deleteMemo(memo.id) },
                                onToggleSelect = {
                                    selectedMemoIds = if (selectedMemoIds.contains(memo.id)) {
                                        selectedMemoIds - memo.id
                                    } else {
                                        selectedMemoIds + memo.id
                                    }
                                    if (selectedMemoIds.isEmpty()) {
                                        isSelectionMode = false
                                    }
                                },
                                onLongPress = {
                                    if (!isSelectionMode) {
                                        isSelectionMode = true
                                        selectedMemoIds = setOf(memo.id)
                                    }
                                }
                            )
                        }
                    }

                    // 普通备忘录
                    val normalMemos = sortedMemos.filter { !it.isPinned }
                    if (normalMemos.isNotEmpty()) {
                        item {
                            MemoSectionHeader(
                                title = "备忘录",
                                count = normalMemos.size
                            )
                        }
                        items(normalMemos, key = { "normal_${it.id}" }) { memo ->
                            SwipeableMemoItem(
                                memo = memo,
                                categories = categories,
                                isSelected = selectedMemoIds.contains(memo.id),
                                isSelectionMode = isSelectionMode,
                                onClick = { if (!isSelectionMode) onMemoClick(memo) },
                                onTogglePin = { viewModel.toggleMemoPin(memo.id) },
                                onDelete = { viewModel.deleteMemo(memo.id) },
                                onToggleSelect = {
                                    selectedMemoIds = if (selectedMemoIds.contains(memo.id)) {
                                        selectedMemoIds - memo.id
                                    } else {
                                        selectedMemoIds + memo.id
                                    }
                                    if (selectedMemoIds.isEmpty()) {
                                        isSelectionMode = false
                                    }
                                },
                                onLongPress = {
                                    if (!isSelectionMode) {
                                        isSelectionMode = true
                                        selectedMemoIds = setOf(memo.id)
                                    }
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableMemoItem(
    memo: Memo,
    categories: List<com.motut.mo.data.MemoCategory>,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    onToggleSelect: () -> Unit,
    onLongPress: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onTogglePin()
                    true
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color by animateColorAsState(
                when (direction) {
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                    SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                },
                label = "swipeColor"
            )
            val alignment = when (direction) {
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                else -> Alignment.Center
            }
            val icon = when (direction) {
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.PushPin
                else -> null
            }
            val desc = when (direction) {
                SwipeToDismissBoxValue.EndToStart -> "删除"
                SwipeToDismissBoxValue.StartToEnd -> if (memo.isPinned) "取消置顶" else "置顶"
                else -> null
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color, MaterialTheme.shapes.medium)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    icon?.let {
                        Icon(it, contentDescription = desc)
                    }
                    if (direction == SwipeToDismissBoxValue.StartToEnd) {
                        Text(
                            text = if (memo.isPinned) "取消置顶" else "置顶",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        },
        content = {
            MemoItem(
                memo = memo,
                categories = categories,
                isSelected = isSelected,
                isSelectionMode = isSelectionMode,
                onClick = onClick,
                onTogglePin = onTogglePin,
                onDelete = onDelete,
                onToggleSelect = onToggleSelect,
                onLongPress = onLongPress
            )
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true
    )
}

@Composable
private fun MemoSectionHeader(
    title: String,
    count: Int
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (title == "置顶") Icons.Default.PushPin else Icons.Default.Notes,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (title == "置顶")
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = CircleShape
        ) {
            Text(
                text = count.toString(),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyMemoState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(40.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "暂无备忘录",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "点击右下角按钮添加新备忘录",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MemoItem(
    memo: Memo,
    categories: List<com.motut.mo.data.MemoCategory>,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit,
    onToggleSelect: () -> Unit,
    onLongPress: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MM-dd HH:mm") }
    val category = remember(memo.categoryId) { categories.find { it.id == memo.categoryId } }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            memo.isPinned -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
            else -> MaterialTheme.colorScheme.surface
        },
        label = "memoBg"
    )

    val categoryColor = category?.let { Color(it.color) } ?: MaterialTheme.colorScheme.primary

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.98f else 1f,
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        onClick = {
            if (isSelectionMode) {
                onToggleSelect()
            } else {
                onClick()
            }
        },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (memo.isPinned) 2.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onLongPress() }
                    )
                }
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isSelectionMode) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { onToggleSelect() },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    if (memo.isPinned) {
                        Icon(
                            imageVector = Icons.Filled.PushPin,
                            contentDescription = "已置顶",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    category?.let { cat ->
                        Surface(
                            modifier = Modifier.size(10.dp),
                            color = Color(cat.color),
                            shape = CircleShape
                        ) {}
                    }
                    Text(
                        text = memo.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }

                if (!isSelectionMode) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        IconButton(
                            onClick = onTogglePin,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (memo.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = if (memo.isPinned) "取消置顶" else "置顶",
                                tint = if (memo.isPinned)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = memo.content.ifBlank { "无内容" },
                style = MaterialTheme.typography.bodyMedium,
                color = if (memo.content.isBlank())
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = memo.createdAt.format(dateFormatter),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                if (category != null) {
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryColor
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // 字数统计
                Text(
                    text = "${memo.content.length}字",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}
