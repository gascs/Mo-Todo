package com.motut.mo.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.motut.mo.data.Memo
import com.motut.mo.viewmodel.AppViewModel

enum class Screen(val title: String, val icon: ImageVector) {
    HOME("首页", Icons.Default.Home),
    MEMO("备忘录", Icons.Default.EditNote),
    TODO("待办", Icons.Default.CheckCircle),
    PROFILE("关于", Icons.Default.Person)
}

sealed class NavDestination {
    data object Main : NavDestination()
    data class MemoDetail(val memo: Memo) : NavDestination()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: AppViewModel = viewModel()) {
    var selectedScreen by remember { mutableStateOf(Screen.HOME) }
    var currentNav by remember { mutableStateOf<NavDestination>(NavDestination.Main) }
    var backPressCount by remember { mutableStateOf(0) }
    
    var showAddMemo by remember { mutableStateOf(false) }
    var showAddTodo by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(SettingsState()) }

    LaunchedEffect(Unit) {
        backPressCount = 0
    }

    when (currentNav) {
        is NavDestination.MemoDetail -> {
            val memo = (currentNav as NavDestination.MemoDetail).memo
            MemoDetailScreen(
                memo = memo,
                onDismiss = { currentNav = NavDestination.Main },
                onSave = { id, title, content, categoryId ->
                    viewModel.updateMemo(id, title, content, categoryId)
                },
                onTogglePin = { viewModel.toggleMemoPin(it) },
                onDelete = { viewModel.deleteMemo(it) }
            )
            return
        }
        NavDestination.Main -> {}
    }

    if (showAddMemo) {
        AddMemoScreen(
            onDismiss = { showAddMemo = false },
            onConfirm = { title, content, categoryId, _ ->
                viewModel.addMemo(title, content, categoryId)
                showAddMemo = false
            }
        )
    } else if (showAddTodo) {
        AddTodoScreen(
            onDismiss = { showAddTodo = false },
            onConfirm = { title, content, location, date, time, priority ->
                viewModel.addTodo(title, content, location, date, time, priority)
                showAddTodo = false
            }
        )
    } else if (showSettings) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("设置") },
                    navigationIcon = {
                        IconButton(onClick = { showSettings = false }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "返回"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SettingsScreen(
                    settings = settings,
                    onSettingsChanged = { settings = it }
                )
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(selectedScreen.title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    Screen.values().forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = selectedScreen == screen,
                            onClick = { selectedScreen = screen },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            },
            floatingActionButton = {
                if (selectedScreen != Screen.PROFILE) {
                    FloatingActionButton(
                        onClick = {
                            when (selectedScreen) {
                                Screen.HOME, Screen.MEMO -> showAddMemo = true
                                Screen.TODO -> showAddTodo = true
                                else -> {}
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = MaterialTheme.shapes.large,
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "添加")
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AnimatedContent(
                    targetState = selectedScreen,
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { if (targetState.ordinal > initialState.ordinal) it else -it },
                            animationSpec = tween(300)
                        ) + fadeIn(animationSpec = tween(300)) togetherWith
                                slideOutHorizontally(
                                    targetOffsetX = { if (targetState.ordinal > initialState.ordinal) -it else it },
                                    animationSpec = tween(300)
                                ) + fadeOut(animationSpec = tween(300))
                    },
                    label = "ScreenAnimation"
                ) { screen ->
                    when (screen) {
                        Screen.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onMemoClick = { memo -> 
                                currentNav = NavDestination.MemoDetail(memo)
                            }
                        )
                        Screen.MEMO -> MemoScreen(
                            viewModel = viewModel,
                            onMemoClick = { memo -> 
                                currentNav = NavDestination.MemoDetail(memo)
                            }
                        )
                        Screen.TODO -> TodoScreen(
                            viewModel = viewModel
                        )
                        Screen.PROFILE -> ProfileScreen(
                            settings = settings,
                            onOpenSettings = { showSettings = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: AppViewModel = viewModel(),
    onMemoClick: (Memo) -> Unit
) {
    val memos by viewModel.memos.collectAsState()
    val sortedMemos = remember(memos) { viewModel.getSortedMemos(memos) }
    val todos by viewModel.todos.collectAsState()
    val categories by viewModel.categories.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        val pendingTodos = todos.filter { !it.isCompleted }
        
        if (pendingTodos.isNotEmpty()) {
            SectionCard(
                title = "待办事项 (${pendingTodos.size})",
                icon = Icons.Default.CheckCircle
            ) {
                pendingTodos.take(5).forEach { todo ->
                    TodoPreviewItem(todo = todo)
                }
                if (pendingTodos.size > 5) {
                    SeeMoreItem(text = "还有 ${pendingTodos.size - 5} 项待办")
                }
            }
        }

        if (sortedMemos.isNotEmpty()) {
            SectionCard(
                title = "备忘录 (${sortedMemos.size})",
                icon = Icons.Default.EditNote
            ) {
                sortedMemos.take(5).forEach { memo ->
                    MemoPreviewItem(
                        memo = memo,
                        categories = categories,
                        onClick = { onMemoClick(memo) }
                    )
                }
                if (memos.size > 5) {
                    SeeMoreItem(text = "还有 ${memos.size - 5} 条备忘录")
                }
            }
        }

        if (sortedMemos.isEmpty() && pendingTodos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        "开始记录你的想法",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "点击右下角按钮添加待办或备忘录",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            }
            content()
        }
    }
}

@Composable
fun TodoPreviewItem(todo: com.motut.mo.data.Todo) {
    val dateFormatter = remember { java.time.format.DateTimeFormatter.ofPattern("MM-dd") }
    
    val priorityColor = when (todo.priority) {
        com.motut.mo.data.Priority.HIGH -> MaterialTheme.colorScheme.error
        com.motut.mo.data.Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        com.motut.mo.data.Priority.LOW -> MaterialTheme.colorScheme.secondary
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(8.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = priorityColor
            ) {}
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (todo.date != null) {
                        Text(
                            text = todo.date.format(dateFormatter),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemoPreviewItem(
    memo: Memo,
    categories: List<com.motut.mo.data.MemoCategory>,
    onClick: () -> Unit
) {
    val category = remember(memo.categoryId) { categories.find { it.id == memo.categoryId } }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (memo.isPinned) 
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) 
        else 
            MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (memo.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                category?.let { cat ->
                    Surface(
                        modifier = Modifier.size(10.dp),
                        color = androidx.compose.ui.graphics.Color(cat.color),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ) {}
                }
                Text(
                    text = memo.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    maxLines = 1
                )
            }
            Text(
                text = memo.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

@Composable
fun SeeMoreItem(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = androidx.compose.ui.graphics.Color.Transparent,
        onClick = {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
