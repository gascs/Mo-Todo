package com.motut.mo.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 设置模块基础组件 - 统一的Dialog、Card、Item模板
 * 解决 P1-5: 已封装 BaseSettingsDialog 但各页面仍内联重复 Dialog 模板
 * 现在所有设置页面统一使用这些基础组件
 */

/**
 * 打开外部URL
 */
fun openUrl(context: android.content.Context, url: String) {
    try {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    } catch (e: Exception) {
        com.motut.mo.util.AppLog.e("Settings", "打开URL失败: $url", e)
    }
}

/**
 * 发送邮件
 */
fun sendEmail(context: android.content.Context, email: String, subject: String = "", body: String = "") {
    try {
        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
            data = "mailto:${email}".toUri()
            putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
            putExtra(android.content.Intent.EXTRA_TEXT, body)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    } catch (e: Exception) {
        com.motut.mo.util.AppLog.e("Settings", "发送邮件失败", e)
    }
}

/**
 * 基础设置对话框 - 统一标题栏+内容区域布局
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseSettingsDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = true)) {
        Scaffold(topBar = {
            TopAppBar(title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) { Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "返回") }
                })
        }) { paddingValues -> content(paddingValues) }
    }
}

/**
 * 设置内容列 - 统一的内边距和滚动行为
 */
@Composable
fun SettingsContentColumn(
    paddingValues: PaddingValues,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)
        .verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
}

/**
 * 设置卡片容器
 */
@Composable
fun SettingsCard(modifier: Modifier = Modifier, containerColor: Color = MaterialTheme.colorScheme.surface,
                 content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().then(modifier),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) { Column(content = content) }
}

/**
 * 设置项（带图标、描述、点击）
 */
@Composable
fun SettingItem(
    title: String, description: String, icon: ImageVector? = null, onClick: () -> Unit
) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            icon?.let {
                Surface(modifier = Modifier.size(40.dp), color = MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape) {
                    Icon(imageVector = it, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.padding(10.dp))
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * 开关设置项（带 Switch）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingSwitchItem(
    title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary))
    }
}
