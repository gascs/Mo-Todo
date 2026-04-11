package com.motut.mo.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 导航组件模块 - 顶部栏、底部导航、FAB等通用导航UI
 */

/**
 * 主屏幕Tab枚举
 */
enum class MainScreenTab(val title: String, val icon: ImageVector) {
    HOME("首页", Icons.Default.Home),
    TASKS("待办", Icons.Default.Checklist),
    NOTES("备忘录", Icons.AutoMirrored.Default.Note),
    CALENDAR("日历", Icons.Default.CalendarToday),
    ME("我的", Icons.Default.Person)
}

/**
 * 设置屏幕枚举
 */
enum class SettingsScreen {
    NONE, NOTIFICATION, APPEARANCE, SYNC, PRIVACY, BACKUP, HELP, ABOUT,
    PRIVACY_POLICY, USER_AGREEMENT, OPEN_SOURCE_STATEMENT, LICENSE
}

/**
 * 主屏幕顶部栏 - 显示当前页面标题 + 搜索按钮
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    selectedTab: MainScreenTab,
    onSearchClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = selectedTab.title,
                style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, "搜索", tint = MaterialTheme.colorScheme.onSurface)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

/**
 * 搜索顶部栏 - 带防抖的搜索输入框
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    onClose: () -> Unit,
    onSearch: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val debounceScope = rememberCoroutineScope()
    val debounceHandler = remember { com.motut.mo.util.DebounceHandler(300L, debounceScope) }

    DisposableEffect(Unit) { onDispose { debounceHandler.cancel() } }

    TopAppBar(
        title = {
            OutlinedTextField(value = searchQuery, onValueChange = {
                searchQuery = it; debounceHandler.debounce { onSearch(it) }
            }, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("搜索任务和备忘录...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true, shape = MaterialTheme.shapes.large)
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "关闭")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

/**
 * 底部导航栏 - 5个主Tab
 */
@Composable
fun MainBottomNavigationBar(
    selectedTab: MainScreenTab,
    onTabSelected: (MainScreenTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.height(64.dp)
    ) {
        MainScreenTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(tab.icon, tab.title,
                        modifier = Modifier.size(if (isSelected) 26.dp else 24.dp))
                },
                label = {
                    Text(tab.title, style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal))
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            )
        }
    }
}
