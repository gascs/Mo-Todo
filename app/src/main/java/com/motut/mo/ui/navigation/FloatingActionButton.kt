package com.motut.mo.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import com.motut.mo.ui.theme.AppColors

/**
 * 主FAB按钮 - 支持展开/收起动画，根据当前Tab显示不同选项
 */
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

    // FAB 旋转动画
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "fabRotation"
    )

    // FAB 缩放动画
    val fabScale by animateFloatAsState(
        targetValue = if (expanded) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "fabScale"
    )

    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 展开菜单项
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(animationSpec = tween(200, delayMillis = 50)) +
                    scaleIn(initialScale = 0.8f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)) +
                    slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(300, easing = FastOutSlowInEasing)),
            exit = fadeOut(animationSpec = tween(150)) +
                    scaleOut(targetScale = 0.8f, animationSpec = tween(200)) +
                    slideOutVertically(targetOffsetY = { it / 2 }, animationSpec = tween(250))
        ) {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // 关闭按钮
                if (showAddTask || showAddNote) {
                    Surface(
                        onClick = onToggle, modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f), shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Close, "关闭",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                if (showAddTask) {
                    FABMenuItem("新建任务", Icons.Default.Checklist, AppColors.PrimaryModern,
                        AppColors.PrimaryModern.copy(alpha = 0.12f), onClick = onAddTask)
                }

                if (showAddNote) {
                    FABMenuItem("新建备忘录", Icons.AutoMirrored.Default.Note, AppColors.AccentModern,
                        AppColors.AccentModern.copy(alpha = 0.12f), onClick = onAddNote)
                }
            }
        }

        // FAB主体
        Surface(
            onClick = onToggle, shape = CircleShape, shadowElevation = 10.dp,
            modifier = Modifier.size(60.dp).graphicsLayer { scaleX = fabScale; scaleY = fabScale }
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(AppColors.GradientPrimary)),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Add, if (expanded) "关闭" else "添加", tint = Color.White,
                    modifier = Modifier.size(28.dp).graphicsLayer { rotationZ = rotation })
            }
        }
    }
}

/**
 * FAB菜单项
 */
@Composable
private fun FABMenuItem(
    text: String, icon: ImageVector, iconColor: Color, backgroundColor: Color, onClick: () -> Unit
) {
    Surface(onClick = onClick, color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp, tonalElevation = 4.dp) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Surface(modifier = Modifier.size(36.dp), color = backgroundColor, shape = CircleShape) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
