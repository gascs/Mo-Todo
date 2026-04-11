package com.motut.mo.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.motut.mo.data.Priority
import com.motut.mo.data.Todo
import com.motut.mo.viewmodel.AppViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val dateFormatter by lazy { DateTimeFormatter.ofPattern("MM-dd") }
private val timeFormatter by lazy { DateTimeFormatter.ofPattern("HH:mm") }

// 排序选项枚举
enum class TodoSortOption(val title: String) {
    DEFAULT("默认"),
    PRIORITY_HIGH("优先级高优先"),
    PRIORITY_LOW("优先级低优先"),
    DATE_NEAR("日期近优先"),
    DATE_FAR("日期远优先")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    viewModel: AppViewModel = viewModel()
) {
    val todos by viewModel.todos.collectAsState()

    // 批量选择状态
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedTodoIds by remember { mutableStateOf(setOf<Long>()) }

    // 排序状态
    var sortOption by remember { mutableStateOf(TodoSortOption.DEFAULT) }
    var showSortMenu by remember { mutableStateOf(false) }

    // 下拉刷新状态
    var isRefreshing by remember { mutableStateOf(false) }

    // 排序后的任务列表
    val sortedTodos by remember(todos, sortOption) {
        derivedStateOf {
            when (sortOption) {
                TodoSortOption.DEFAULT -> todos.sortedByDescending { it.createdAt }
                TodoSortOption.PRIORITY_HIGH -> todos.sortedBy { it.priority.ordinal }
                TodoSortOption.PRIORITY_LOW -> todos.sortedByDescending { it.priority.ordinal }
                TodoSortOption.DATE_NEAR -> todos.sortedBy { it.date ?: LocalDate.MAX }
                TodoSortOption.DATE_FAR -> todos.sortedByDescending { it.date ?: LocalDate.MIN }
            }
        }
    }

    val pendingTodos by remember(sortedTodos) {
        derivedStateOf { sortedTodos.filter { !it.isCompleted } }
    }
    val completedTodos by remember(sortedTodos) {
        derivedStateOf { sortedTodos.filter { it.isCompleted } }
    }

    // 刷新模拟
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            kotlinx.coroutines.delay(800)
            isRefreshing = false
        }
    }

    // 批量删除选中任务
    val deleteSelectedTodos = {
        selectedTodoIds.forEach { id ->
            viewModel.deleteTodo(id)
        }
        selectedTodoIds = emptySet()
        isSelectionMode = false
    }

    // 批量标记完成
    val completeSelectedTodos = {
        selectedTodoIds.forEach { id ->
            viewModel.toggleTodoCompletion(id)
        }
        selectedTodoIds = emptySet()
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
                            selectedTodoIds = emptySet()
                        }) {
                            Icon(Icons.Default.Close, "取消")
                        }
                        Text(
                            "已选择 ${selectedTodoIds.size} 项",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = completeSelectedTodos) {
                            Icon(Icons.Default.CheckCircle, "完成")
                        }
                        IconButton(onClick = deleteSelectedTodos) {
                            Icon(Icons.Default.Delete, "删除", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        // 排序选项栏
        if (!isSelectionMode && todos.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "共 ${todos.size} 个任务",
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
                        TodoSortOption.entries.forEach { option ->
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

        if (sortedTodos.isEmpty()) {
            EmptyTodoState()
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
                    if (pendingTodos.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "待完成",
                                count = pendingTodos.size,
                                accentColor = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(pendingTodos, key = { it.id }) { todo ->
                            SwipeableTodoItem(
                                todo = todo,
                                isSelected = selectedTodoIds.contains(todo.id),
                                isSelectionMode = isSelectionMode,
                                onToggle = { viewModel.toggleTodoCompletion(todo.id) },
                                onDelete = { viewModel.deleteTodo(todo.id) },
                                onToggleSelect = {
                                    selectedTodoIds = if (selectedTodoIds.contains(todo.id)) {
                                        selectedTodoIds - todo.id
                                    } else {
                                        selectedTodoIds + todo.id
                                    }
                                    if (selectedTodoIds.isEmpty()) {
                                        isSelectionMode = false
                                    }
                                },
                                onLongPress = {
                                    if (!isSelectionMode) {
                                        isSelectionMode = true
                                        selectedTodoIds = setOf(todo.id)
                                    }
                                }
                            )
                        }
                    }

                    if (completedTodos.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            SectionHeader(
                                title = "已完成",
                                count = completedTodos.size,
                                accentColor = MaterialTheme.colorScheme.outline
                            )
                        }
                        items(
                            completedTodos.take(5),
                            key = { it.id }
                        ) { todo ->
                            SwipeableTodoItem(
                                todo = todo,
                                isSelected = selectedTodoIds.contains(todo.id),
                                isSelectionMode = isSelectionMode,
                                onToggle = { viewModel.toggleTodoCompletion(todo.id) },
                                onDelete = { viewModel.deleteTodo(todo.id) },
                                onToggleSelect = {
                                    selectedTodoIds = if (selectedTodoIds.contains(todo.id)) {
                                        selectedTodoIds - todo.id
                                    } else {
                                        selectedTodoIds + todo.id
                                    }
                                    if (selectedTodoIds.isEmpty()) {
                                        isSelectionMode = false
                                    }
                                },
                                onLongPress = {
                                    if (!isSelectionMode) {
                                        isSelectionMode = true
                                        selectedTodoIds = setOf(todo.id)
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
private fun SwipeableTodoItem(
    todo: Todo,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onToggleSelect: () -> Unit,
    onLongPress: () -> Unit
) {
    var isSwipeOpen by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onToggle()
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
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Check
                else -> null
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color, MaterialTheme.shapes.medium)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                icon?.let {
                    Icon(
                        it,
                        contentDescription = null,
                        tint = when (direction) {
                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.onErrorContainer
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }
        },
        content = {
            TodoItem(
                todo = todo,
                isSelected = isSelected,
                isSelectionMode = isSelectionMode,
                onToggle = onToggle,
                onDelete = onDelete,
                onToggleSelect = onToggleSelect,
                onLongPress = onLongPress
            )
        },
        enableDismissFromStartToEnd = !todo.isCompleted,
        enableDismissFromEndToStart = true
    )
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    accentColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(4.dp, 16.dp)
                .background(accentColor, CircleShape)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Surface(
            color = accentColor.copy(alpha = 0.15f),
            shape = CircleShape
        ) {
            Text(
                text = count.toString(),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

@Composable
private fun EmptyTodoState() {
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
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "暂无待办事项",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "点击右下角按钮添加新待办",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoItem(
    todo: Todo,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onToggleSelect: () -> Unit,
    onLongPress: () -> Unit
) {
    val priorityColor = when (todo.priority) {
        Priority.HIGH -> MaterialTheme.colorScheme.error
        Priority.MEDIUM -> Color(0xFFF59E0B)
        Priority.LOW -> Color(0xFF10B981)
    }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            todo.isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            todo.priority == Priority.HIGH -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
            else -> MaterialTheme.colorScheme.surface
        },
        label = "bgColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.98f else 1f,
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress() },
                    onTap = {
                        if (isSelectionMode) {
                            onToggleSelect()
                        }
                    }
                )
            },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelect() }
                )
            } else {
                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (todo.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (todo.content.isNotBlank() && !todo.isCompleted) {
                    Text(
                        text = todo.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (todo.date != null) {
                        MetaTag(
                            icon = Icons.Default.CalendarToday,
                            text = todo.date.format(dateFormatter),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    if (todo.time != null) {
                        MetaTag(
                            icon = Icons.Default.AccessTime,
                            text = todo.time.format(timeFormatter),
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    if (todo.location.isNotBlank()) {
                        MetaTag(
                            icon = Icons.Default.LocationOn,
                            text = todo.location,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    color = priorityColor.copy(alpha = 0.15f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = when (todo.priority) {
                            Priority.HIGH -> "高"
                            Priority.MEDIUM -> "中"
                            Priority.LOW -> "低"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = priorityColor
                    )
                }

                if (!isSelectionMode) {
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
    }
}

@Composable
private fun MetaTag(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = color
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, LocalDate?, LocalTime?, Priority) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showReminderPicker by remember { mutableStateOf(false) }
    var reminderMinutes by remember { mutableStateOf(10) }
    var hasReminder by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf<androidx.compose.ui.graphics.Color?>(null) }
    var showColorPicker by remember { mutableStateOf(false) }

    val colorOptions = listOf(
        null to "默认",
        androidx.compose.ui.graphics.Color(0xFFE57373) to "红色",
        androidx.compose.ui.graphics.Color(0xFFFFB74D) to "橙色",
        androidx.compose.ui.graphics.Color(0xFFFFF176) to "黄色",
        androidx.compose.ui.graphics.Color(0xFF81C784) to "绿色",
        androidx.compose.ui.graphics.Color(0xFF64B5F6) to "蓝色",
        androidx.compose.ui.graphics.Color(0xFFBA68C8) to "紫色"
    )

    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text("选择颜色标签") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colorOptions.forEach { (color, name) ->
                        Surface(
                            onClick = {
                                selectedColor = color
                                showColorPicker = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                if (color != null) {
                                    Surface(
                                        modifier = Modifier.size(24.dp),
                                        color = color,
                                        shape = CircleShape
                                    ) {}
                                } else {
                                    Surface(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = CircleShape
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Block,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(name)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showColorPicker = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showReminderPicker) {
        AlertDialog(
            onDismissRequest = { showReminderPicker = false },
            title = { Text("设置提醒") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5, 10, 15, 30, 60).forEach { minutes ->
                        Surface(
                            onClick = {
                                reminderMinutes = minutes
                                hasReminder = true
                                showReminderPicker = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                RadioButton(
                                    selected = reminderMinutes == minutes && hasReminder,
                                    onClick = {
                                        reminderMinutes = minutes
                                        hasReminder = true
                                        showReminderPicker = false
                                    }
                                )
                                Text("${minutes}分钟前")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReminderPicker = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDatePicker = false }
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    DatePicker(
                        state = datePickerState,
                        title = { Text("选择日期") }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("取消")
                        }
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    selectedDate = java.time.Instant.ofEpochMilli(it)
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDate()
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("确定")
                        }
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showTimePicker = false }
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "选择时间",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    TimePicker(state = timePickerState)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text("取消")
                        }
                        TextButton(
                            onClick = {
                                selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                                showTimePicker = false
                            }
                        ) {
                            Text("确定")
                        }
                    }
                }
            }
        }
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "添加待办事项",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "关闭"
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("详细内容") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("地点") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (selectedDate != null) {
                                selectedDate!!.format(DateTimeFormatter.ofPattern("MM月dd日"))
                            } else {
                                "选择日期"
                            }
                        )
                    }
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (selectedTime != null) {
                                selectedTime!!.format(DateTimeFormatter.ofPattern("HH:mm"))
                            } else {
                                "选择时间"
                            }
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "重要程度",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilterChip(
                            selected = selectedPriority == Priority.LOW,
                            onClick = { selectedPriority = Priority.LOW },
                            label = { Text("低") },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                        FilterChip(
                            selected = selectedPriority == Priority.MEDIUM,
                            onClick = { selectedPriority = Priority.MEDIUM },
                            label = { Text("中") },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                        FilterChip(
                            selected = selectedPriority == Priority.HIGH,
                            onClick = { selectedPriority = Priority.HIGH },
                            label = { Text("高") },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showColorPicker = true },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (selectedColor != null) {
                            Surface(
                                modifier = Modifier.size(18.dp),
                                color = selectedColor!!,
                                shape = CircleShape
                            ) {}
                        } else {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("标签")
                    }
                    OutlinedButton(
                        onClick = { showReminderPicker = true },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = if (hasReminder) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (hasReminder) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (hasReminder) "${reminderMinutes}分钟" else "提醒")
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("取消")
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, content, location, selectedDate, selectedTime, selectedPriority)
                            }
                        },
                        enabled = title.isNotBlank(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("添加")
                    }
                }
            }
        }
    }
}
