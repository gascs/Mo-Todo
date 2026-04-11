package com.motut.mo.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.motut.mo.data.Memo
import androidx.compose.ui.unit.sp
import com.motut.mo.data.Priority
import com.motut.mo.data.Todo
import com.motut.mo.data.brandColor
import com.motut.mo.data.themeColor
import com.motut.mo.ui.theme.AppColors
import kotlinx.coroutines.delay

/**
 * 渐变统计卡片 - 用于首页的待办/已完成/备忘录统计展示
 */
@Composable
fun GradientStatCard(
    title: String,
    count: Int,
    icon: ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )

    Surface(
        modifier = modifier.scale(scale),
        onClick = { isPressed = true },
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = Brush.linearGradient(gradientColors))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    color = Color.White.copy(alpha = 0.25f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(10.dp).size(24.dp)
                    )
                }
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
        LaunchedEffect(isPressed) {
            if (isPressed) { delay(100); isPressed = false }
        }
    }
}

/**
 * 快速查看任务区域
 */
@Composable
fun QuickTasksSection(
    todos: List<Todo>,
    onTodoClick: (Todo) -> Unit,
    onToggleComplete: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            todos.forEach { todo ->
                QuickTaskItem(todo = todo, onClick = { onTodoClick(todo) }, onToggleComplete = { onToggleComplete(todo.id) })
            }
        }
    }
}

/**
 * 单个快捷任务条目
 */
@Composable
fun QuickTaskItem(
    todo: Todo,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit
) {
    val priorityColor = todo.priority.brandColor

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
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
        Surface(modifier = Modifier.size(6.dp), color = priorityColor, shape = CircleShape) {}
        Text(
            text = todo.title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f).clickable(onClick = onClick),
            maxLines = 1
        )
    }
}

/**
 * 快速查看备忘录区域
 */
@Composable
fun QuickNotesSection(
    memos: List<Memo>,
    onMemoClick: (Memo) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            memos.forEach { memo ->
                QuickNoteItem(memo = memo, onClick = { onMemoClick(memo) })
            }
        }
    }
}

/**
 * 单个快捷备忘录条目
 */
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
