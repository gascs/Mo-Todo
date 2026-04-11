package com.motut.mo.ui.notes

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.motut.mo.data.Memo
import com.motut.mo.util.DateFormats
import kotlinx.coroutines.delay

/**
 * 单个备忘录卡片 - 支持点击动画、置顶切换、字数统计
 */
@Composable
fun NoteItemCard(
    memo: Memo,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "noteScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale },
        onClick = { isPressed = true; onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 标题行 + 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = memo.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (memo.isPinned) {
                        Surface(
                            modifier = Modifier.size(20.dp),
                            color = contentColor.copy(alpha = 0.2f),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "已置顶",
                                tint = contentColor,
                                modifier = Modifier.padding(4.dp).size(12.dp)
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(onClick = onTogglePin, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = if (memo.isPinned) "取消置顶" else "置顶",
                            tint = if (memo.isPinned) contentColor else contentColor.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "删除",
                            tint = contentColor.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // 内容
            if (memo.content.isNotBlank()) {
                Text(
                    text = memo.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.75f),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 底部信息栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = memo.createdAt.format(DateFormats.dateTime),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.5f)
                )
                if (memo.content.isNotBlank()) {
                    Text(
                        text = "${memo.content.length}字",
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) { delay(100); isPressed = false }
    }
}
