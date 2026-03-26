package com.motut.mo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.LabelOff
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.motut.mo.data.Memo
import com.motut.mo.data.Todo
import com.motut.mo.ui.components.EmptyState
import com.motut.mo.ui.components.RatingPromptDialog
import com.motut.mo.viewmodel.AppViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

private val dateFormatter by lazy { DateTimeFormatter.ofPattern("yyyy年MM月dd日") }
private val shortDateFormatter by lazy { DateTimeFormatter.ofPattern("MM-dd") }
private val timeFormatter by lazy { DateTimeFormatter.ofPattern("HH:mm") }

enum class MainScreenTab(val title: String, val icon: ImageVector) {
    HOME("首页", Icons.Default.Home),
    TASKS("待办", Icons.Default.Checklist),
    NOTES("备忘录", Icons.AutoMirrored.Default.Note),
    CALENDAR("日历", Icons.Default.CalendarToday),
    ME("我的", Icons.Default.Person)
}

enum class SettingsScreen {
    NONE,
    NOTIFICATION,
    APPEARANCE,
    SYNC,
    PRIVACY,
    BACKUP,
    HELP,
    ABOUT,
    PRIVACY_POLICY,
    OPEN_SOURCE_STATEMENT,
    LICENSE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenV2(
    viewModel: AppViewModel = viewModel(),
    onBackPressed: ((Boolean) -> Unit)? = null
) {
    var selectedTab by remember { mutableStateOf(MainScreenTab.HOME) }
    var showFabMenu by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddTask by remember { mutableStateOf(false) }
    var showAddNote by remember { mutableStateOf(false) }
    var showMemoDetail by remember { mutableStateOf<Memo?>(null) }
    var showTaskDetail by remember { mutableStateOf<Todo?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var currentSettingsScreen by remember { mutableStateOf(SettingsScreen.NONE) }
    var showRatingPrompt by remember { mutableStateOf(false) }
    var appLaunchCount by remember { mutableIntStateOf(0) }

    val todos by viewModel.todos.collectAsState()
    val memos by viewModel.memos.collectAsState()
    val sortedMemos by remember(memos) { derivedStateOf { viewModel.getSortedMemos(memos) } }
    
    val searchResults = remember(todos, memos, searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                emptyList<Todo>() to emptyList<Memo>()
            } else {
                val query = searchQuery.lowercase()
                val matchedTodos = todos.filter { 
                    it.title.lowercase().contains(query) || 
                    it.content.lowercase().contains(query)
                }
                val matchedMemos = memos.filter { 
                    it.title.lowercase().contains(query) || 
                    it.content.lowercase().contains(query)
                }
                matchedTodos to matchedMemos
            }
        }
    }
    
    val hasPendingTodos by remember {
        derivedStateOf { todos.any { !it.isCompleted } }
    }
    
    val pendingTodosCount by remember {
        derivedStateOf { todos.count { !it.isCompleted } }
    }
    
    val completedTodosCount by remember {
        derivedStateOf { todos.count { it.isCompleted } }
    }
    
    val hasMemos by remember {
        derivedStateOf { memos.isNotEmpty() }
    }
    
    LaunchedEffect(Unit) {
        appLaunchCount++
        if (appLaunchCount % 5 == 0) {
            showRatingPrompt = true
        }
    }

    val canGoBack = remember(
        showSearch, 
        showAddTask, 
        showAddNote, 
        showMemoDetail, 
        showTaskDetail, 
        currentSettingsScreen
    ) {
        showSearch || 
        showAddTask || 
        showAddNote || 
        showMemoDetail != null || 
        showTaskDetail != null || 
        currentSettingsScreen != SettingsScreen.NONE
    }

    BackHandler(enabled = canGoBack) {
        when {
            showSearch -> {
                showSearch = false
                searchQuery = ""
            }
            showAddTask -> {
                showAddTask = false
            }
            showAddNote -> {
                showAddNote = false
            }
            showMemoDetail != null -> {
                showMemoDetail = null
            }
            showTaskDetail != null -> {
                showTaskDetail = null
            }
            currentSettingsScreen != SettingsScreen.NONE -> {
                when (currentSettingsScreen) {
                    SettingsScreen.PRIVACY_POLICY, 
                    SettingsScreen.OPEN_SOURCE_STATEMENT, 
                    SettingsScreen.LICENSE -> {
                        currentSettingsScreen = SettingsScreen.ABOUT
                    }
                    else -> {
                        currentSettingsScreen = SettingsScreen.NONE
                    }
                }
            }
        }
    }

    LaunchedEffect(canGoBack) {
        onBackPressed?.invoke(canGoBack)
    }

    Scaffold(
        topBar = {
            if (showSearch) {
                SearchTopBar(
                    onClose = { 
                        showSearch = false 
                        searchQuery = ""
                    },
                    onSearch = { searchQuery = it }
                )
            } else {
                MainTopBar(
                    selectedTab = selectedTab,
                    onSearchClick = { showSearch = true }
                )
            }
        },
        bottomBar = {
            MainBottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        floatingActionButton = {
            if (selectedTab != MainScreenTab.ME) {
                MainFAB(
                    expanded = showFabMenu,
                    selectedTab = selectedTab,
                    onToggle = { showFabMenu = !showFabMenu },
                    onAddTask = {
                        showFabMenu = false
                        showAddTask = true
                    },
                    onAddNote = {
                        showFabMenu = false
                        showAddNote = true
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (showSearch) {
                SearchResultsScreen(
                    searchQuery = searchQuery,
                    matchedTodos = searchResults.value.first,
                    matchedMemos = searchResults.value.second,
                    onTodoClick = { todo -> 
                        showSearch = false
                        searchQuery = ""
                        showTaskDetail = todo
                    },
                    onMemoClick = { memo -> 
                        showSearch = false
                        searchQuery = ""
                        showMemoDetail = memo
                    }
                )
            } else {
                AnimatedContent(
                    targetState = selectedTab,
                    label = "ScreenTransition",
                    transitionSpec = {
                        val isForward = targetState.ordinal > initialState.ordinal
                        slideInHorizontally(
                            initialOffsetX = { if (isForward) it else -it },
                            animationSpec = tween(
                                durationMillis = 180,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeIn(animationSpec = tween(
                            durationMillis = 150,
                            easing = LinearEasing
                        )) togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { if (isForward) -it else it },
                            animationSpec = tween(
                                durationMillis = 180,
                                easing = FastOutSlowInEasing
                            )
                        ) + fadeOut(animationSpec = tween(
                            durationMillis = 150,
                            easing = LinearEasing
                        ))
                    }
                ) { tab ->
                when (tab) {
                    MainScreenTab.HOME -> HomeDashboardScreen(
                        todos = todos,
                        memos = sortedMemos,
                        onTodoClick = { todo -> showTaskDetail = todo },
                        onMemoClick = { memo -> showMemoDetail = memo },
                        onAddTaskClick = { 
                            showFabMenu = false
                            showAddTask = true
                        },
                        onAddNoteClick = { 
                            showFabMenu = false
                            showAddNote = true
                        },
                        onToggleComplete = { viewModel.toggleTodoCompletion(it) }
                    )
                    MainScreenTab.TASKS -> TasksScreen(
                        todos = todos,
                        viewModel = viewModel,
                        onTodoClick = { todo -> showTaskDetail = todo },
                        onAddTaskClick = { 
                            showFabMenu = false
                            showAddTask = true
                        }
                    )
                    MainScreenTab.NOTES -> NotesScreen(
                        memos = sortedMemos,
                        viewModel = viewModel,
                        onMemoClick = { memo -> showMemoDetail = memo },
                        onAddNoteClick = { 
                            showFabMenu = false
                            showAddNote = true
                        }
                    )
                    MainScreenTab.CALENDAR -> CalendarScreen(
                        todos = todos,
                        onTodoClick = { todo -> showTaskDetail = todo }
                    )
                    MainScreenTab.ME -> MeScreen(
                        onSettingsClick = { action ->
                            when (action) {
                                "通知设置" -> currentSettingsScreen = SettingsScreen.NOTIFICATION
                                "外观主题" -> currentSettingsScreen = SettingsScreen.APPEARANCE
                                "数据同步" -> currentSettingsScreen = SettingsScreen.SYNC
                                "隐私与安全" -> currentSettingsScreen = SettingsScreen.PRIVACY
                                "数据备份" -> currentSettingsScreen = SettingsScreen.BACKUP
                                "帮助与反馈" -> currentSettingsScreen = SettingsScreen.HELP
                                "关于Mo" -> currentSettingsScreen = SettingsScreen.ABOUT
                                else -> {
                                    snackbarMessage = "$action 功能开发中..."
                                    showSnackbar = true
                                }
                            }
                        }
                    )
                }
                }
            }
        }
    }

    if (showSnackbar) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            showSnackbar = false
        }
        Snackbar(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(snackbarMessage)
        }
    }

    if (showAddTask) {
        AddTaskBottomSheet(
            onDismiss = { showAddTask = false },
            onConfirm = { title, content, location, date, time, priority ->
                viewModel.addTodo(title, content, location, date, time, priority)
                showAddTask = false
                snackbarMessage = "任务已添加！"
                showSnackbar = true
            }
        )
    }

    if (showAddNote) {
        AddMemoScreen(
            onDismiss = { showAddNote = false },
            onConfirm = { title, content, categoryId, attachments ->
                viewModel.addMemo(title, content, categoryId)
                showAddNote = false
                snackbarMessage = "备忘录已添加！"
                showSnackbar = true
            }
        )
    }

    showMemoDetail?.let { memo ->
        MemoDetailScreen(
            memo = memo,
            onDismiss = { showMemoDetail = null },
            onSave = { id, title, content, categoryId ->
                viewModel.updateMemo(id, title, content, categoryId)
                snackbarMessage = "备忘录已保存！"
                showSnackbar = true
            },
            onTogglePin = { viewModel.toggleMemoPin(it) },
            onDelete = { 
                viewModel.deleteMemo(it)
                showMemoDetail = null
                snackbarMessage = "备忘录已删除！"
                showSnackbar = true
            }
        )
    }

    showTaskDetail?.let { todo ->
        TaskDetailBottomSheet(
            todo = todo,
            onDismiss = { showTaskDetail = null },
            onToggleComplete = { viewModel.toggleTodoCompletion(it) },
            onDelete = { 
                viewModel.deleteTodo(it)
                showTaskDetail = null
                snackbarMessage = "任务已删除！"
                showSnackbar = true
            }
        )
    }

    AnimatedContent(
        targetState = currentSettingsScreen,
        label = "SettingsScreenTransition",
        transitionSpec = {
            val isForward = targetState.ordinal > initialState.ordinal
            val enter = if (isForward) {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(
                        durationMillis = 160,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(
                    durationMillis = 130,
                    easing = LinearEasing
                ))
            } else {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(
                        durationMillis = 160,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(
                    durationMillis = 130,
                    easing = LinearEasing
                ))
            }
            val exit = if (isForward) {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(
                        durationMillis = 160,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(
                    durationMillis = 130,
                    easing = LinearEasing
                ))
            } else {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(
                        durationMillis = 160,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(animationSpec = tween(
                    durationMillis = 130,
                    easing = LinearEasing
                ))
            }
            enter togetherWith exit
        }
    ) { screen ->
        when (screen) {
            SettingsScreen.NOTIFICATION -> NotificationSettingsScreen(
                onDismiss = { currentSettingsScreen = SettingsScreen.NONE }
            )
            SettingsScreen.APPEARANCE -> AppearanceSettingsScreen(
                onDismiss = { currentSettingsScreen = SettingsScreen.NONE }
            )
            SettingsScreen.SYNC -> SyncSettingsScreen(
                onDismiss = { currentSettingsScreen = SettingsScreen.NONE }
            )
            SettingsScreen.PRIVACY -> PrivacySettingsScreen(
                onDismiss = { currentSettingsScreen = SettingsScreen.NONE }
            )
            SettingsScreen.BACKUP -> BackupSettingsScreen(
                viewModel = viewModel,
                onDismiss = { currentSettingsScreen = SettingsScreen.NONE },
                onSnackbar = { 
                    snackbarMessage = it
                    showSnackbar = true
                }
            )
            SettingsScreen.HELP -> HelpFeedbackScreen(
                onDismiss = { currentSettingsScreen = SettingsScreen.NONE }
            )
            SettingsScreen.ABOUT -> AboutMoScreen(
                onDismiss = { currentSettingsScreen = SettingsScreen.NONE },
                onNavigateToPrivacyPolicy = { currentSettingsScreen = SettingsScreen.PRIVACY_POLICY },
                onNavigateToOpenSourceStatement = { currentSettingsScreen = SettingsScreen.OPEN_SOURCE_STATEMENT },
                onNavigateToLicense = { currentSettingsScreen = SettingsScreen.LICENSE }
            )
            SettingsScreen.PRIVACY_POLICY -> PrivacyPolicyScreen(
                onDismiss = { currentSettingsScreen = SettingsScreen.ABOUT }
            )
            SettingsScreen.OPEN_SOURCE_STATEMENT -> OpenSourceStatementScreen(
                onDismiss = { currentSettingsScreen = SettingsScreen.ABOUT }
            )
            SettingsScreen.LICENSE -> LicenseScreen(
                onDismiss = { currentSettingsScreen = SettingsScreen.ABOUT }
            )
            SettingsScreen.NONE -> {}
        }
    }
    
    if (showRatingPrompt) {
        RatingPromptDialog(
            onDismiss = { showRatingPrompt = false },
            onRate = { 
                showRatingPrompt = false 
            },
            onLater = { 
                showRatingPrompt = false 
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    selectedTab: MainScreenTab,
    onSearchClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                Text(
                    text = selectedTab.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = {}) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    onClose: () -> Unit,
    onSearch: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val debounceHandler = remember { com.motut.mo.util.DebounceHandler(300L) }
    
    DisposableEffect(Unit) {
        onDispose {
            debounceHandler.cancel()
        }
    }
    
    TopAppBar(
        title = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    debounceHandler.debounce {
                        onSearch(it)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("搜索任务和备忘录...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "关闭"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun MainBottomNavigationBar(
    selectedTab: MainScreenTab,
    onTabSelected: (MainScreenTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        MainScreenTab.values().forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(tab.icon, contentDescription = tab.title) },
                label = { Text(tab.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            )
        }
    }
}

@Composable
fun MainFAB(
    expanded: Boolean,
    selectedTab: MainScreenTab,
    onToggle: () -> Unit,
    onAddTask: () -> Unit,
    onAddNote: () -> Unit
) {
    val showAddTask = when (selectedTab) {
        MainScreenTab.HOME, MainScreenTab.TASKS, MainScreenTab.CALENDAR -> true
        else -> false
    }
    val showAddNote = when (selectedTab) {
        MainScreenTab.HOME, MainScreenTab.NOTES -> true
        else -> false
    }

    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 150,
                    easing = FastOutLinearInEasing
                )
            ) + slideInVertically(
                initialOffsetY = { it }, 
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = 100,
                    easing = LinearOutSlowInEasing
                )
            ) + slideOutVertically(
                targetOffsetY = { it }, 
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (showAddTask) {
                    Surface(
                        onClick = onAddTask,
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 3.dp,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Text(
                                "新建任务", 
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if (showAddNote) {
                    Surface(
                        onClick = onAddNote,
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 3.dp,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Text(
                                "新建备忘录", 
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.Note,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onToggle,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(6.dp)
        ) {
            AnimatedContent(
                targetState = expanded,
                label = "FABIcon",
                transitionSpec = {
                    fadeIn(
                        animationSpec = spring(
                            dampingRatio = 1f,
                            stiffness = 400f
                        )
                    ) togetherWith 
                    fadeOut(
                        animationSpec = spring(
                            dampingRatio = 1f,
                            stiffness = 400f
                        )
                    )
                }
            ) { isExpanded ->
                Icon(
                    imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (isExpanded) "关闭" else "添加",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
fun HomeDashboardScreen(
    todos: List<Todo>,
    memos: List<Memo>,
    onTodoClick: (Todo) -> Unit,
    onMemoClick: (Memo) -> Unit,
    onAddTaskClick: () -> Unit,
    onAddNoteClick: () -> Unit,
    onToggleComplete: (Long) -> Unit = {}
) {
    val currentDate = remember { LocalDateTime.now() }
    val dayOfWeek = remember { currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINA) }
    
    val pendingTodos by remember(todos) {
        derivedStateOf { todos.filter { !it.isCompleted } }
    }
    val completedTodos by remember(todos) {
        derivedStateOf { todos.filter { it.isCompleted } }
    }
    
    val greeting = remember {
        when (currentDate.hour) {
            in 0..11 -> "早上好"
            in 12..17 -> "下午好"
            else -> "晚上好"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$greeting",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${currentDate.format(dateFormatter)} · $dayOfWeek",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "待办",
                count = pendingTodos.size,
                icon = Icons.Default.CheckCircle,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "已完成",
                count = completedTodos.size,
                icon = Icons.Default.Done,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "备忘录",
                count = memos.size,
                icon = Icons.AutoMirrored.Default.Note,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }

        if (pendingTodos.isNotEmpty()) {
            QuickTasksSection(
                todos = pendingTodos.take(5),
                onTodoClick = onTodoClick,
                onToggleComplete = onToggleComplete
            )
        }

        if (memos.isNotEmpty()) {
            QuickNotesSection(
                memos = memos.take(3),
                onMemoClick = onMemoClick
            )
        }

        if (memos.isEmpty() && pendingTodos.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Add,
                title = "开始记录你的想法",
                description = "点击右下角按钮添加待办或备忘录",
                actionText = "新建",
                onActionClick = { onAddTaskClick() }
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun SummaryCard(
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickTasksSection(
    todos: List<Todo>,
    onTodoClick: (Todo) -> Unit,
    onToggleComplete: (Long) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "快速查看",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            todos.forEach { todo ->
                QuickTaskItem(
                    todo = todo,
                    onClick = { onTodoClick(todo) },
                    onToggleComplete = { onToggleComplete(todo.id) }
                )
            }
        }
    }
}

@Composable
fun QuickTaskItem(
    todo: Todo,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit
) {
    val priorityColor = when (todo.priority) {
        com.motut.mo.data.Priority.HIGH -> MaterialTheme.colorScheme.error
        com.motut.mo.data.Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        com.motut.mo.data.Priority.LOW -> MaterialTheme.colorScheme.secondary
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = false,
            onCheckedChange = { onToggleComplete() },
            modifier = Modifier.size(32.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary
            )
        )
        Surface(
            modifier = Modifier.size(6.dp),
            color = priorityColor,
            shape = CircleShape
        ) {}
        Text(
            text = todo.title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClick),
            maxLines = 1
        )
    }
}

@Composable
fun QuickNotesSection(
    memos: List<Memo>,
    onMemoClick: (Memo) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "最近备忘录",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            memos.forEach { memo ->
                QuickNoteItem(
                    memo = memo,
                    onClick = { onMemoClick(memo) }
                )
            }
        }
    }
}

@Composable
fun QuickNoteItem(
    memo: Memo,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = memo.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
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
fun TodayTasksCard(todosCount: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "今日待办",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "还有 $todosCount 项未完成",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Text(
                text = todosCount.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun UpcomingTasksSection(
    todos: List<Todo>,
    onTodoClick: (Todo) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "即将到期",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            todos.forEach { todo ->
                UpcomingTaskCard(
                    todo = todo,
                    onClick = { onTodoClick(todo) }
                )
            }
        }
    }
}

@Composable
fun UpcomingTaskCard(
    todo: Todo,
    onClick: () -> Unit
) {
    val priorityColor = when (todo.priority) {
        com.motut.mo.data.Priority.HIGH -> MaterialTheme.colorScheme.error
        com.motut.mo.data.Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        com.motut.mo.data.Priority.LOW -> MaterialTheme.colorScheme.secondary
    }
    
    Surface(
        modifier = Modifier.width(260.dp),
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(6.dp),
                    color = priorityColor,
                    shape = CircleShape
                ) {}
                val timeText = remember(todo.date, todo.time) {
                    buildString {
                        todo.date?.let { append(it.format(shortDateFormatter)) }
                        todo.time?.let { 
                            if (isNotEmpty()) append(" ")
                            append(it.format(timeFormatter))
                        }
                    }
                }
                if (timeText.isNotBlank()) {
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = todo.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            if (todo.content.isNotBlank()) {
                Text(
                    text = todo.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun RecentNotesSection(
    memos: List<Memo>,
    onMemoClick: (Memo) -> Unit
) {
    val colors = listOf(
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer,
        MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "最近备忘录",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            memos.forEachIndexed { index, memo ->
                val (containerColor, contentColor) = colors[index % 4]
                RecentNoteCard(
                    memo = memo,
                    containerColor = containerColor,
                    contentColor = contentColor,
                    onClick = { onMemoClick(memo) }
                )
            }
        }
    }
}

@Composable
fun RecentNoteCard(
    memo: Memo,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.width(150.dp),
        onClick = onClick,
        color = containerColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = memo.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = contentColor
                )
                if (memo.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = memo.content,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.7f),
                maxLines = 4
            )
        }
    }
}

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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (pendingTodos.isNotEmpty()) {
                    item {
                        TaskGroupHeader(title = "待完成", count = pendingTodos.size)
                    }
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
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        TaskGroupHeader(title = "已完成", count = completedTodos.size)
                    }
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
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = CircleShape
        ) {
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

@Composable
fun TaskItemCard(
    todo: Todo,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (todo.priority) {
        com.motut.mo.data.Priority.HIGH -> MaterialTheme.colorScheme.error
        com.motut.mo.data.Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        com.motut.mo.data.Priority.LOW -> MaterialTheme.colorScheme.secondary
    }
    
    AnimatedContent(
        targetState = todo.isCompleted,
        label = "TaskCompletion",
        transitionSpec = {
            fadeIn(animationSpec = tween(200)) togetherWith 
            fadeOut(animationSpec = tween(150))
        }
    ) { isCompleted ->
        Surface(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
            color = if (isCompleted)
                Color.Transparent
            else if (todo.priority == com.motut.mo.data.Priority.HIGH)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.08f)
            else
                Color.Transparent,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleComplete() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = if (isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium
                    )
                    if (todo.content.isNotBlank() && !isCompleted) {
                        Text(
                            text = todo.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isCompleted) {
                        Surface(
                            modifier = Modifier.size(8.dp),
                            color = priorityColor,
                            shape = CircleShape
                        ) {}
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotesScreen(
    memos: List<Memo>,
    viewModel: AppViewModel,
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
            Row(
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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

private val noteDateFormatter by lazy { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm") }

@Composable
fun NoteItemCard(
    memo: Memo,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit
) {
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = memo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onTogglePin, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = if (memo.isPinned) "取消置顶" else "置顶",
                            tint = if (memo.isPinned) contentColor else contentColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            if (memo.content.isNotBlank()) {
                Text(
                    text = memo.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.7f),
                    maxLines = 3
                )
            }
            Text(
                text = memo.createdAt.format(noteDateFormatter),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.5f)
            )
        }
    }
}

private val monthFormatter by lazy { DateTimeFormatter.ofPattern("yyyy年MM月") }
private val calendarDateFormatter by lazy { DateTimeFormatter.ofPattern("yyyy年MM月dd日") }
private val calendarTimeFormatter by lazy { DateTimeFormatter.ofPattern("HH:mm") }

@Composable
fun CalendarScreen(
    todos: List<Todo>,
    onTodoClick: (Todo) -> Unit
) {
    val today = remember { LocalDate.now() }
    var selectedMonth by remember { mutableStateOf(today) }
    var selectedDate by remember { mutableStateOf(today) }
    val daysOfWeek = listOf("日", "一", "二", "三", "四", "五", "六")
    
    val firstDayOfMonth = selectedMonth.withDayOfMonth(1)
    val daysInMonth = selectedMonth.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    
    val selectedDateTodos = remember(todos, selectedDate) {
        todos.filter { it.date == selectedDate && !it.isCompleted }
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
            IconButton(onClick = { selectedMonth = selectedMonth.minusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "上个月"
                )
            }
            Text(
                text = selectedMonth.format(monthFormatter),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = { selectedMonth = selectedMonth.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "下个月"
                )
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                val weeks = (daysInMonth + firstDayOfWeek + 6) / 7
                repeat(weeks) { weekIndex ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(7) { dayIndex ->
                            val dayNumber = weekIndex * 7 + dayIndex - firstDayOfWeek + 1
                            val currentDateItem = if (dayNumber in 1..31) {
                                selectedMonth.withDayOfMonth(dayNumber)
                            } else {
                                null
                            }
                            val isToday = currentDateItem == today
                            val isSelected = currentDateItem == selectedDate
                            val hasEvent = currentDateItem?.let { date ->
                                todos.any { it.date == date && !it.isCompleted }
                            } ?: false
                            
                            val canClick = currentDateItem != null
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            isToday -> MaterialTheme.colorScheme.primaryContainer
                                            else -> Color.Transparent
                                        }
                                    )
                                    .let { if (canClick) it.clickable { currentDateItem?.let { selectedDate = it } } else it },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = if (dayNumber in 1..31) dayNumber.toString() else "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = when {
                                            isSelected -> MaterialTheme.colorScheme.onPrimary
                                            isToday -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                    if (hasEvent && dayNumber in 1..31) {
                                        Surface(
                                            modifier = Modifier.size(4.dp),
                                            color = when {
                                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                                isToday -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.primary
                                            },
                                            shape = CircleShape
                                        ) {}
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${selectedDate.format(calendarDateFormatter)} 安排",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            if (selectedDateTodos.isEmpty()) {
                Text(
                    text = "这一天没有安排",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                selectedDateTodos.forEach { todo ->
                    CalendarEventCard(
                        todo = todo,
                        onClick = { onTodoClick(todo) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarEventCard(
    todo: Todo,
    onClick: () -> Unit
) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error
    )
    val color = colors[todo.id.toInt() % 3]
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp),
                color = color,
                shape = RoundedCornerShape(2.dp)
            ) {}
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                todo.time?.let {
                    Text(
                        text = it.format(calendarTimeFormatter),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun MeScreen(
    onSettingsClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ProfileHeader()
        SettingsSection(onSettingsClick = onSettingsClick)
        AboutSection(onSettingsClick = onSettingsClick)
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ProfileHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Text(
                text = "MoTuT",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "高效工作，简单生活",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun SettingsSection(
    onSettingsClick: (String) -> Unit
) {
    val settingsItems = listOf(
        Icons.Default.Notifications to "通知设置",
        Icons.Default.Palette to "外观主题",
        Icons.Default.Cloud to "数据同步",
        Icons.Default.Security to "隐私与安全",
        Icons.Default.Backup to "数据备份"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            settingsItems.forEachIndexed { index, (icon, title) ->
                SettingsItem(
                    icon = icon,
                    title = title,
                    onClick = { onSettingsClick(title) },
                    showDivider = index < settingsItems.size - 1
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    showDivider: Boolean
) {
    Column {
        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            color = androidx.compose.ui.graphics.Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
fun AboutSection(
    onSettingsClick: (String) -> Unit
) {
    val aboutItems = listOf(
        Icons.AutoMirrored.Default.Help to "帮助与反馈",
        Icons.Default.Info to "关于Mo"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            aboutItems.forEachIndexed { index, (icon, title) ->
                SettingsItem(
                    icon = icon,
                    title = title,
                    onClick = { onSettingsClick(title) },
                    showDivider = index < aboutItems.size - 1
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, LocalDate?, java.time.LocalTime?, com.motut.mo.data.Priority) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<java.time.LocalTime?>(null) }
    var selectedPriority by remember { mutableStateOf(com.motut.mo.data.Priority.MEDIUM) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    val initialDateMillis = remember(selectedDate) {
        selectedDate?.atStartOfDay(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)
    
    val initialHour = remember(selectedTime) { selectedTime?.hour ?: 0 }
    val initialMinute = remember(selectedTime) { selectedTime?.minute ?: 0 }
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute
    )
    
    if (showDatePicker) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showDatePicker = false }) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DatePicker(state = datePickerState)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("取消")
                        }
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let {
                                selectedDate = java.time.Instant.ofEpochMilli(it)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                            showDatePicker = false
                        }) {
                            Text("确定")
                        }
                    }
                }
            }
        }
    }
    
    if (showTimePicker) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showTimePicker = false }) {
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
                        TextButton(onClick = {
                            selectedTime = java.time.LocalTime.of(timePickerState.hour, timePickerState.minute)
                            showTimePicker = false
                        }) {
                            Text("确定")
                        }
                    }
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "新建任务",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("任务标题") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )
            
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("详细描述") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
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
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
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
                    text = "优先级",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedPriority == com.motut.mo.data.Priority.LOW,
                        onClick = { selectedPriority = com.motut.mo.data.Priority.LOW },
                        label = { Text("低") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedPriority == com.motut.mo.data.Priority.MEDIUM,
                        onClick = { selectedPriority = com.motut.mo.data.Priority.MEDIUM },
                        label = { Text("中") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedPriority == com.motut.mo.data.Priority.HIGH,
                        onClick = { selectedPriority = com.motut.mo.data.Priority.HIGH },
                        label = { Text("高") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("取消")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onConfirm(title, content, location, selectedDate, selectedTime, selectedPriority)
                        }
                    },
                    enabled = title.isNotBlank(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("创建任务")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "新建备忘录",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("标题") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )
            
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("内容") },
                minLines = 5,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("取消")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onConfirm(title, content)
                        }
                    },
                    enabled = title.isNotBlank(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("保存备忘录")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailBottomSheet(
    todo: Todo,
    onDismiss: () -> Unit,
    onToggleComplete: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    val priorityText = when (todo.priority) {
        com.motut.mo.data.Priority.HIGH -> "高"
        com.motut.mo.data.Priority.MEDIUM -> "中"
        com.motut.mo.data.Priority.LOW -> "低"
    }
    val priorityColor = when (todo.priority) {
        com.motut.mo.data.Priority.HIGH -> MaterialTheme.colorScheme.error
        com.motut.mo.data.Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        com.motut.mo.data.Priority.LOW -> MaterialTheme.colorScheme.secondary
    }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "任务详情",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭"
                    )
                }
            }
            
            Surface(
                color = priorityColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = priorityText,
                    style = MaterialTheme.typography.labelSmall,
                    color = priorityColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = todo.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            if (todo.content.isNotBlank()) {
                Text(
                    text = todo.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                todo.date?.let {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = it.format(dateFormatter),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
                todo.time?.let {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = it.format(timeFormatter),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { 
                        onToggleComplete(todo.id)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(
                        imageVector = if (todo.isCompleted) Icons.Default.RadioButtonUnchecked else Icons.Default.CheckCircle,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (todo.isCompleted) "标记未完成" else "标记完成")
                }
                Button(
                    onClick = { onDelete(todo.id) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("删除")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SearchResultsScreen(
    searchQuery: String,
    matchedTodos: List<Todo>,
    matchedMemos: List<Memo>,
    onTodoClick: (Todo) -> Unit,
    onMemoClick: (Memo) -> Unit
) {
    val colors = listOf(
        MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer,
        MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (searchQuery.isBlank()) {
            EmptyState(
                icon = Icons.Default.Search,
                title = "搜索你的任务和备忘录",
                description = "输入关键词开始搜索"
            )
        } else if (matchedTodos.isEmpty() && matchedMemos.isEmpty()) {
            EmptyState(
                icon = Icons.Default.SearchOff,
                title = "未找到结果",
                description = "尝试使用其他关键词搜索"
            )
        } else {
            if (matchedTodos.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "任务 (${matchedTodos.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    matchedTodos.forEach { todo ->
                        TaskItemCard(
                            todo = todo,
                            onClick = { onTodoClick(todo) },
                            onToggleComplete = {},
                            onDelete = {}
                        )
                    }
                }
            }
            
            if (matchedMemos.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.Note,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "备忘录 (${matchedMemos.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    matchedMemos.forEachIndexed { index, memo ->
                        val (containerColor, contentColor) = colors[index % 4]
                        NoteItemCard(
                            memo = memo,
                            containerColor = containerColor,
                            contentColor = contentColor,
                            onClick = { onMemoClick(memo) },
                            onTogglePin = {},
                            onDelete = {}
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
