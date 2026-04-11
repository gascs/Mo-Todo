package com.motut.mo.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motut.mo.data.Memo
import com.motut.mo.data.Todo
import com.motut.mo.ui.components.EmptyState
import com.motut.mo.ui.theme.AppColors
import com.motut.mo.util.DateFormats
import com.motut.mo.util.dayOfWeekChinese
import com.motut.mo.util.greeting
import com.motut.mo.util.greetingEmoji
import java.time.LocalDateTime
import kotlinx.coroutines.delay

/**
 * 首页仪表盘屏幕 - 展示概览统计、快速任务、快速备忘录
 */
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
    val dayOfWeek = remember { currentDate.toLocalDate().dayOfWeekChinese() }

    val pendingTodos by remember(todos) {
        derivedStateOf { todos.filter { !it.isCompleted } }
    }
    val completedTodos by remember(todos) {
        derivedStateOf { todos.filter { it.isCompleted } }
    }

    val greeting = remember(currentDate.hour) { currentDate.toLocalTime().hour.greeting() }

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 欢迎区域
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(500)) +
                    slideInVertically(initialOffsetY = { -20 })
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val emoji = currentDate.toLocalTime().hour.greetingEmoji()
                    Text(text = emoji, style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = "${currentDate.format(DateFormats.fullChinese)} · $dayOfWeek",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                )
            }
        }

        // 统计卡片区域
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 100)) +
                    slideInVertically(initialOffsetY = { 20 })
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientStatCard(
                    title = "待办",
                    count = pendingTodos.size,
                    icon = Icons.Default.CheckCircle,
                    gradientColors = AppColors.StatGradientTodo,
                    modifier = Modifier.weight(1f)
                )
                GradientStatCard(
                    title = "已完成",
                    count = completedTodos.size,
                    icon = Icons.Default.Done,
                    gradientColors = AppColors.StatGradientCompleted,
                    modifier = Modifier.weight(1f)
                )
                GradientStatCard(
                    title = "备忘录",
                    count = memos.size,
                    icon = Icons.AutoMirrored.Default.Note,
                    gradientColors = AppColors.StatGradientMemo,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 快捷任务区域
        if (pendingTodos.isNotEmpty()) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 200)) +
                        slideInVertically(initialOffsetY = { 20 })
            ) {
                QuickTasksSection(
                    todos = pendingTodos.take(5),
                    onTodoClick = onTodoClick,
                    onToggleComplete = onToggleComplete
                )
            }
        }

        // 快捷备忘录区域
        if (memos.isNotEmpty()) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 300)) +
                        slideInVertically(initialOffsetY = { 20 })
            ) {
                QuickNotesSection(memos = memos.take(3), onMemoClick = onMemoClick)
            }
        }

        // 空状态
        if (memos.isEmpty() && pendingTodos.isEmpty()) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 400))
            ) {
                EmptyState(
                    icon = Icons.Default.Add,
                    title = "开始记录你的想法",
                    description = "点击右下角按钮添加待办或备忘录",
                    actionText = "新建",
                    onActionClick = { onAddTaskClick() }
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
