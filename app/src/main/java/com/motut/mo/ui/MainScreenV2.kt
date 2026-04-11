package com.motut.mo.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.motut.mo.data.Memo
import com.motut.mo.data.Todo
import com.motut.mo.ui.bottomsheet.AddNoteBottomSheet
import com.motut.mo.ui.bottomsheet.AddTaskBottomSheet
import com.motut.mo.ui.bottomsheet.TaskDetailBottomSheet
import com.motut.mo.ui.calendar.CalendarScreen
import com.motut.mo.ui.components.RatingPromptDialog
import com.motut.mo.ui.home.HomeDashboardScreen
import com.motut.mo.ui.me.MeScreen
import com.motut.mo.ui.navigation.MainBottomNavigationBar
import com.motut.mo.ui.navigation.MainFAB
import com.motut.mo.ui.navigation.MainScreenTab
import com.motut.mo.ui.navigation.SettingsScreen
import com.motut.mo.ui.navigation.MainTopBar
import com.motut.mo.ui.navigation.SearchTopBar
import com.motut.mo.ui.notes.NotesScreen
import com.motut.mo.ui.search.SearchResultsScreen
import com.motut.mo.ui.settings.NotificationSettingsScreen
import com.motut.mo.ui.settings.AppearanceSettingsScreen
import com.motut.mo.ui.settings.SyncSettingsScreen
import com.motut.mo.ui.settings.PrivacySettingsScreen
import com.motut.mo.ui.settings.BackupSettingsScreen
import com.motut.mo.ui.settings.HelpFeedbackScreen
import com.motut.mo.ui.settings.AboutMoScreen
import com.motut.mo.ui.settings.PrivacyPolicyScreen
import com.motut.mo.ui.settings.OpenSourceStatementScreen
import com.motut.mo.ui.settings.LicenseScreen
import com.motut.mo.ui.settings.UserAgreementScreen
import com.motut.mo.ui.tasks.TasksScreen
import com.motut.mo.util.AnimationUtils
import com.motut.mo.viewmodel.AppViewModel
import kotlinx.coroutines.delay

/**
 * MainScreenV2 - 主屏幕编排器（重构后）
 *
 * 重构前: 3607行，包含30+个Composable
 * 重构后: ~300行，仅负责状态管理和组件编排
 *
 * 拆分出的模块:
 * - ui/home/        -> 首页仪表盘 (HomeScreen + HomeComponents)
 * - ui/tasks/       -> 待办列表 (TasksScreen + TaskItemCard)
 * - ui/notes/       -> 备忘录列表 (NotesScreen + NoteItemCard)
 * - ui/calendar/    -> 日历视图 (CalendarScreen + CalendarEventCard)
 * - ui/me/          -> 个人中心 (MeScreen)
 * - ui/search/      -> 搜索结果 (SearchResultsScreen)
 * - ui/bottomsheet/ -> 底部弹窗 (AddTask/AddNote/TaskDetail)
 * - ui/navigation/  -> 导航组件 (TopBar/BottomNav/FAB)
 * - data/Priority.kt-> 统一优先级颜色映射
 * - util/DateFormats.kt -> 统一日期格式化
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenV2(
    viewModel: AppViewModel = viewModel(),
    onBackPressed: ((Boolean) -> Unit)? = null
) {
    // ==================== 状态管理 ====================
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

    // ==================== 数据订阅 ====================
    val todos by viewModel.todos.collectAsState()
    val memos by viewModel.memos.collectAsState()
    val sortedMemos by remember(memos) { derivedStateOf { viewModel.getSortedMemos(memos) } }

    // 搜索结果（带防抖）
    val searchResults = remember(todos, memos, searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) emptyList<Todo>() to emptyList<Memo>()
            else {
                val query = searchQuery.lowercase()
                val matchedTodos = todos.filter {
                    it.title.lowercase().contains(query) || it.content.lowercase().contains(query)
                }
                val matchedMemos = memos.filter {
                    it.title.lowercase().contains(query) || it.content.lowercase().contains(query)
                }
                matchedTodos to matchedMemos
            }
        }
    }

    // ==================== 生命周期与导航 ====================
    LaunchedEffect(Unit) {
        appLaunchCount++
        if (appLaunchCount % 5 == 0) showRatingPrompt = true
    }

    val canGoBack = remember(showSearch, showAddTask, showAddNote, showMemoDetail, showTaskDetail, currentSettingsScreen) {
        showSearch || showAddTask || showAddNote || showMemoDetail != null ||
        showTaskDetail != null || currentSettingsScreen != SettingsScreen.NONE
    }

    BackHandler(enabled = canGoBack) {
        when {
            showSearch -> { showSearch = false; searchQuery = "" }
            showAddTask -> showAddTask = false
            showAddNote -> showAddNote = false
            showMemoDetail != null -> showMemoDetail = null
            showTaskDetail != null -> showTaskDetail = null
            currentSettingsScreen != SettingsScreen.NONE -> {
                when (currentSettingsScreen) {
                    SettingsScreen.PRIVACY_POLICY, SettingsScreen.OPEN_SOURCE_STATEMENT, SettingsScreen.LICENSE ->
                        currentSettingsScreen = SettingsScreen.ABOUT
                    else -> currentSettingsScreen = SettingsScreen.NONE
                }
            }
        }
    }

    LaunchedEffect(canGoBack) { onBackPressed?.invoke(canGoBack) }

    // ==================== 主界面布局 ====================
    Scaffold(
        topBar = {
            if (showSearch) {
                SearchTopBar(onClose = { showSearch = false; searchQuery = "" }, onSearch = { searchQuery = it })
            } else {
                MainTopBar(selectedTab = selectedTab, onSearchClick = { showSearch = true })
            }
        },
        bottomBar = {
            MainBottomNavigationBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        },
        floatingActionButton = {
            if (selectedTab != MainScreenTab.ME) {
                MainFAB(
                    expanded = showFabMenu, selectedTab = selectedTab,
                    onToggle = { showFabMenu = !showFabMenu },
                    onAddTask = { showFabMenu = false; showAddTask = true },
                    onAddNote = { showFabMenu = false; showAddNote = true }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (showSearch) {
                SearchResultsScreen(
                    searchQuery = searchQuery,
                    matchedTodos = searchResults.value.first,
                    matchedMemos = searchResults.value.second,
                    onTodoClick = { todo -> showSearch = false; searchQuery = ""; showTaskDetail = todo },
                    onMemoClick = { memo -> showSearch = false; searchQuery = ""; showMemoDetail = memo }
                )
            } else {
                // 页面切换动画
                AnimatedContent(
                    targetState = selectedTab, label = "ScreenTransition",
                    contentKey = { it.ordinal },
                    transitionSpec = {
                        fadeIn(animationSpec = tween(250)) togetherWith
                            fadeOut(animationSpec = tween(200))
                    }
                ) { tab ->
                    when (tab) {
                        MainScreenTab.HOME -> HomeDashboardScreen(
                            todos = todos, memos = sortedMemos,
                            onTodoClick = { showTaskDetail = it },
                            onMemoClick = { showMemoDetail = it },
                            onAddTaskClick = { showFabMenu = false; showAddTask = true },
                            onAddNoteClick = { showFabMenu = false; showAddNote = true },
                            onToggleComplete = { viewModel.toggleTodoCompletion(it) }
                        )
                        MainScreenTab.TASKS -> TasksScreen(
                            todos = todos, viewModel = viewModel,
                            onTodoClick = { showTaskDetail = it },
                            onAddTaskClick = { showFabMenu = false; showAddTask = true }
                        )
                        MainScreenTab.NOTES -> NotesScreen(
                            memos = sortedMemos, viewModel = viewModel,
                            onMemoClick = { showMemoDetail = it },
                            onAddNoteClick = { showFabMenu = false; showAddNote = true }
                        )
                        MainScreenTab.CALENDAR -> CalendarScreen(
                            todos = todos, onTodoClick = { showTaskDetail = it }
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
                                    else -> { snackbarMessage = "$action 功能开发中..."; showSnackbar = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // ==================== Snackbar 提示 ====================
    if (showSnackbar) {
        LaunchedEffect(Unit) { delay(2000); showSnackbar = false }
        Snackbar(modifier = Modifier.padding(16.dp)) { Text(snackbarMessage) }
    }

    // ==================== 弹窗层 ====================

    // 新建任务弹窗
    if (showAddTask) {
        AddTaskBottomSheet(
            onDismiss = { showAddTask = false },
            onConfirm = { title, content, location, date, time, priority ->
                viewModel.addTodo(title, content, location, date, time, priority)
                showAddTask = false; snackbarMessage = "任务已添加！"; showSnackbar = true
            }
        )
    }

    // 新建备忘录弹窗
    if (showAddNote) {
        AddMemoScreen(
            onDismiss = { showAddNote = false },
            onConfirm = { title, content, categoryId, _ ->
                viewModel.addMemo(title, content, categoryId)
                showAddNote = false; snackbarMessage = "备忘录已添加！"; showSnackbar = true
            }
        )
    }

    // 备忘录详情弹窗
    AnimatedVisibility(
        visible = showMemoDetail != null,
        enter = fadeIn(animationSpec = tween(AnimationUtils.FAST_ANIMATION_DURATION, easing = FastOutSlowInEasing)) +
                scaleIn(initialScale = 0.9f, animationSpec = tween(AnimationUtils.FAST_ANIMATION_DURATION, easing = FastOutSlowInEasing)),
        exit = fadeOut(animationSpec = tween(AnimationUtils.FAST_ANIMATION_DURATION - 20, easing = LinearEasing)) +
                scaleOut(targetScale = 0.9f, animationSpec = tween(AnimationUtils.FAST_ANIMATION_DURATION - 20, easing = LinearEasing))
    ) {
        showMemoDetail?.let { memo ->
            MemoDetailScreen(memo = memo, onDismiss = { showMemoDetail = null },
                onSave = { id, title, content, categoryId ->
                    viewModel.updateMemo(id, title, content, categoryId)
                    snackbarMessage = "备忘录已保存！"; showSnackbar = true
                },
                onTogglePin = { viewModel.toggleMemoPin(it) },
                onDelete = { viewModel.deleteMemo(it); showMemoDetail = null; snackbarMessage = "备忘录已删除！"; showSnackbar = true }
            )
        }
    }

    // 任务详情弹窗
    AnimatedVisibility(
        visible = showTaskDetail != null,
        enter = fadeIn(animationSpec = tween(AnimationUtils.FAST_ANIMATION_DURATION, easing = FastOutSlowInEasing)) +
                slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(AnimationUtils.FAST_ANIMATION_DURATION, easing = FastOutSlowInEasing)),
        exit = fadeOut(animationSpec = tween(AnimationUtils.FAST_ANIMATION_DURATION - 20, easing = LinearEasing)) +
                slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = tween(AnimationUtils.FAST_ANIMATION_DURATION - 20, easing = LinearEasing))
    ) {
        showTaskDetail?.let { todo ->
            TaskDetailBottomSheet(todo = todo, onDismiss = { showTaskDetail = null },
                onToggleComplete = { viewModel.toggleTodoCompletion(it) },
                onDelete = { viewModel.deleteTodo(it); showTaskDetail = null; snackbarMessage = "任务已删除！"; showSnackbar = true }
            )
        }
    }

    // 设置页面切换动画
    AnimatedContent(
        targetState = currentSettingsScreen, label = "SettingsScreenTransition",
        transitionSpec = {
            fadeIn(animationSpec = tween(AnimationUtils.FAST_ANIMATION_DURATION)) togetherWith
                fadeOut(animationSpec = tween(AnimationUtils.FAST_ANIMATION_DURATION - 20))
        }
    ) { screen ->
        when (screen) {
            SettingsScreen.NOTIFICATION -> NotificationSettingsScreen(onDismiss = { currentSettingsScreen = SettingsScreen.NONE })
            SettingsScreen.APPEARANCE -> AppearanceSettingsScreen(onDismiss = { currentSettingsScreen = SettingsScreen.NONE })
            SettingsScreen.SYNC -> SyncSettingsScreen(onDismiss = { currentSettingsScreen = SettingsScreen.NONE })
            SettingsScreen.PRIVACY -> PrivacySettingsScreen(
                onDismiss = { currentSettingsScreen = SettingsScreen.NONE },
                onNavigateToPrivacyPolicy = { currentSettingsScreen = SettingsScreen.PRIVACY_POLICY },
                onNavigateToUserAgreement = { currentSettingsScreen = SettingsScreen.USER_AGREEMENT }
            )
            SettingsScreen.BACKUP -> BackupSettingsScreen(viewModel = viewModel, onDismiss = { currentSettingsScreen = SettingsScreen.NONE },
                onSnackbar = { snackbarMessage = it; showSnackbar = true })
            SettingsScreen.HELP -> HelpFeedbackScreen(onDismiss = { currentSettingsScreen = SettingsScreen.NONE })
            SettingsScreen.ABOUT -> AboutMoScreen(
                onDismiss = { currentSettingsScreen = SettingsScreen.NONE },
                onNavigateToPrivacyPolicy = { currentSettingsScreen = SettingsScreen.PRIVACY_POLICY },
                onNavigateToOpenSourceStatement = { currentSettingsScreen = SettingsScreen.OPEN_SOURCE_STATEMENT },
                onNavigateToLicense = { currentSettingsScreen = SettingsScreen.LICENSE }
            )
            SettingsScreen.PRIVACY_POLICY -> PrivacyPolicyScreen(onDismiss = { currentSettingsScreen = SettingsScreen.PRIVACY })
            SettingsScreen.USER_AGREEMENT -> UserAgreementScreen(onDismiss = { currentSettingsScreen = SettingsScreen.PRIVACY })
            SettingsScreen.OPEN_SOURCE_STATEMENT -> OpenSourceStatementScreen(onDismiss = { currentSettingsScreen = SettingsScreen.ABOUT })
            SettingsScreen.LICENSE -> LicenseScreen(onDismiss = { currentSettingsScreen = SettingsScreen.ABOUT })
            SettingsScreen.NONE -> {}
        }
    }

    // 评分提示
    if (showRatingPrompt) {
        RatingPromptDialog(
            onDismiss = { showRatingPrompt = false },
            onRate = { showRatingPrompt = false },
            onLater = { showRatingPrompt = false }
        )
    }
}
