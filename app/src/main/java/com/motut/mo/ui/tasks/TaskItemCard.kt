package com.motut.mo.ui.tasks

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.motut.mo.data.Todo
import com.motut.mo.data.brandColor
import com.motut.mo.ui.theme.AppColors
import kotlinx.coroutines.delay

/**
 * 单个任务卡片 - 支持点击动画、优先级颜色、完成状态切换动画
 */
@Composable
fun TaskItemCard(
    todo: Todo,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = todo.priority.brandColor

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "cardScale"
    )

    AnimatedContent(
        targetState = todo.isCompleted,
        label = "TaskCompletion",
        transitionSpec = {
            fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(150))
        }
    ) { isCompleted ->
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { scaleX = scale; scaleY = scale },
            onClick = { isPressed = true; onClick() },
            color = when {
                isCompleted -> Color.Transparent
                todo.priority == com.motut.mo.data.Priority.HIGH -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.05f)
                else -> MaterialTheme.colorScheme.surface
            },
            shape = RoundedCornerShape(16.dp),
            shadowElevation = if (isCompleted) 0.dp else 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleComplete() },
                    modifier = Modifier.size(48.dp),
                    colors = CheckboxDefaults.colors(checkedColor = AppColors.SuccessModern)
                )

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium
                        ),
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                 else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (todo.content.isNotBlank() && !isCompleted) {
                        Text(
                            text = todo.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!isCompleted) {
                        Surface(modifier = Modifier.size(10.dp), color = priorityColor, shape = CircleShape) {}
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        LaunchedEffect(isPressed) {
            if (isPressed) { delay(100); isPressed = false }
        }
    }
}
