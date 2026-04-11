package com.motut.mo.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.motut.mo.data.Todo
import com.motut.mo.data.Priority
import com.motut.mo.ui.theme.AppColors

// ==================== 动画常量 ====================
private const val ANIMATION_DURATION_MS = 300
private const val QUICK_ANIMATION_MS = 150
private val ANIMATION_EASING = FastOutSlowInEasing

// ==================== 可访问性图标按钮 ====================
@Composable
fun AccessibleIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    iconSize: Int = 24,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize.dp)
        )
    }
}

// ==================== 动画过渡按钮 ====================
@Composable
fun AnimatedScaleButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .scale(scale)
            .size(56.dp),
        color = backgroundColor,
        shape = CircleShape,
        shadowElevation = 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable { isPressed = true }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            LaunchedEffect(isPressed) {
                if (isPressed) {
                    kotlinx.coroutines.delay(100)
                    isPressed = false
                }
            }
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// ==================== 渐变卡片 ====================
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = AppColors.GradientPrimary,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(gradientColors)
                ),
            content = content
        )
    }
}

// ==================== 玻璃态卡片 ====================
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.let { bg ->
        // 简单判断背景是否偏暗
        val red = (bg.red * 255).toInt()
        val green = (bg.green * 255).toInt()
        val blue = (bg.blue * 255).toInt()
        (red + green + blue) / 3 < 128
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isDark) {
                            listOf(
                                AppColors.GlassDark,
                                AppColors.GlassDark.copy(alpha = 0.5f)
                            )
                        } else {
                            listOf(
                                AppColors.GlassWhite,
                                AppColors.GlassWhite.copy(alpha = 0.3f)
                            )
                        }
                    )
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isDark) {
                            listOf(
                                Color(0x15FFFFFF),
                                Color(0x05FFFFFF)
                            )
                        } else {
                            listOf(
                                Color(0x20FFFFFF),
                                Color(0x10FFFFFF)
                            )
                        }
                    )
                ),
            content = content
        )
    }
}

// ==================== 统计卡片（美化版） ====================
@Composable
fun EnhancedSummaryCard(
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
        modifier = modifier
            .scale(scale)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isPressed = true
            },
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(gradientColors)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(24.dp)
                    )
                }
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
        
        LaunchedEffect(isPressed) {
            if (isPressed) {
                kotlinx.coroutines.delay(100)
                isPressed = false
            }
        }
    }
}

// ==================== 空状态组件 ====================
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 动画图标
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { visible = true }
        
        // 动画脉冲效果
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(500)) +
                    scaleIn(initialScale = 0.5f, animationSpec = spring(dampingRatio = 0.5f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                // 脉冲背景
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(pulseScale),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {}
                Surface(
                    modifier = Modifier.size(100.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(500, delayMillis = 200)) +
                    slideInVertically(initialOffsetY = { it / 2 })
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        actionText?.let {
            Spacer(modifier = Modifier.height(32.dp))
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 400))
            ) {
                Button(
                    onClick = { onActionClick?.invoke() },
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(it)
                }
            }
        }
    }
}

// ==================== 骨架屏加载器 ====================
@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier,
    itemCount: Int = 3
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(itemCount) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(20.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
                            )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
                            )
                    )
                }
            }
        }
    }
}

// ==================== 可访问性复选框 ====================
@Composable
fun AccessibleCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.size(48.dp),
        enabled = enabled,
        colors = CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colorScheme.primary
        )
    )
}

// ==================== 实时区域文本（无障碍） ====================
@Composable
fun LiveRegionText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier
    )
}

// ==================== 优先级标签 ====================
@Composable
fun PriorityBadge(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (priority) {
        Priority.HIGH -> AppColors.PriorityHighModern to "高"
        Priority.MEDIUM -> AppColors.PriorityMediumModern to "中"
        Priority.LOW -> AppColors.PriorityLowModern to "低"
    }

    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ==================== 状态指示器 ====================
@Composable
fun StatusIndicator(
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isCompleted) {
        AppColors.SuccessModern
    } else {
        MaterialTheme.colorScheme.outline
    }

    val alpha by animateFloatAsState(
        targetValue = if (isCompleted) 1f else 0.5f,
        animationSpec = tween(ANIMATION_DURATION_MS),
        label = "statusAlpha"
    )

    Surface(
        modifier = modifier.size(12.dp),
        color = color.copy(alpha = alpha),
        shape = CircleShape
    ) {}
}

// ==================== 折叠内容组件 ====================
@Composable
fun ExpandableContent(
    title: String,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(ANIMATION_DURATION_MS, easing = ANIMATION_EASING),
        label = "rotation"
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = if (expanded) "折叠" else "展开",
                modifier = Modifier.graphicsLayer { rotationZ = rotationAngle }
            )
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = tween(ANIMATION_DURATION_MS)) +
                    fadeIn(),
            exit = shrinkVertically(animationSpec = tween(ANIMATION_DURATION_MS)) +
                    fadeOut()
        ) {
            content()
        }
    }
}

// ==================== 评分提示对话框 ====================
@Composable
fun RatingPromptDialog(
    onDismiss: () -> Unit,
    onRate: () -> Unit,
    onLater: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(24.dp)
                    )
                }
                Text(
                    text = "喜欢Mo吗？",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "如果您觉得Mo好用，请给我们一个好评吧！",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onLater,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("稍后再说")
                    }
                    Button(
                        onClick = onRate,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("去评分")
                    }
                }
            }
        }
    }
}

// ==================== Toast/Snackbar 增强 ====================
@Composable
fun AnimatedSnackbar(
    message: String,
    action: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        action = {
            action?.let {
                TextButton(onClick = { onActionClick?.invoke() }) {
                    Text(it)
                }
            }
        },
        dismissAction = {
            IconButton(onClick = { /* Dismiss */ }) {
                Icon(Icons.Default.Close, contentDescription = "关闭")
            }
        }
    ) {
        Text(message, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

// ==================== 任务滑动手势卡片 ====================
@Composable
fun SwipeableTaskCard(
    todo: Todo,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "swipeOffset"
    )
    
    val priorityColor = when (todo.priority) {
        com.motut.mo.data.Priority.HIGH -> MaterialTheme.colorScheme.error
        com.motut.mo.data.Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        com.motut.mo.data.Priority.LOW -> MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        // 删除背景
        if (animatedOffset < -50f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error)
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        }
        
        // 完成背景
        if (animatedOffset > 50f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.SuccessModern)
                    .padding(start = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "完成",
                    tint = Color.White
                )
            }
        }
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
            color = if (todo.priority == com.motut.mo.data.Priority.HIGH)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surface,
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
                    checked = todo.isCompleted,
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
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        textDecoration = if (todo.isCompleted) 
                            androidx.compose.ui.text.style.TextDecoration.LineThrough 
                        else 
                            androidx.compose.ui.text.style.TextDecoration.None,
                        color = if (todo.isCompleted) 
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) 
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                    if (todo.content.isNotBlank() && !todo.isCompleted) {
                        Text(
                            text = todo.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!todo.isCompleted) {
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
