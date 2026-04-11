package com.motut.mo.ui.me

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * "我的"页面 - 个人中心，包含头像、设置入口、关于信息
 */
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "M", style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
            Text(text = "MoTuT", style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(text = "高效工作，简单生活", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun SettingsSection(onSettingsClick: (String) -> Unit) {
    val settingsItems = listOf(
        Icons.Default.Notifications to "通知设置",
        Icons.Default.Palette to "外观主题",
        Icons.Default.Cloud to "数据同步",
        Icons.Default.Security to "隐私与安全",
        Icons.Default.Backup to "数据备份"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            settingsItems.forEachIndexed { index, (icon, title) ->
                SettingsItem(icon = icon, title = title, onClick = { onSettingsClick(title) }, showDivider = index < settingsItems.size - 1)
            }
        }
    }
}

@Composable
fun AboutSection(onSettingsClick: (String) -> Unit) {
    val aboutItems = listOf(
        Icons.AutoMirrored.Default.Help to "帮助与反馈",
        Icons.Default.Info to "关于Mo"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            aboutItems.forEachIndexed { index, (icon, title) ->
                SettingsItem(icon = icon, title = title, onClick = { onSettingsClick(title) }, showDivider = index < aboutItems.size - 1)
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    showDivider: Boolean
) {
    Column {
        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape) {
                    Icon(imageVector = icon, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.padding(10.dp))
                }
                Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (showDivider) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
        }
    }
}
