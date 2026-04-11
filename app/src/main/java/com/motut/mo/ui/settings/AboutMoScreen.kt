package com.motut.mo.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.motut.mo.MoApplication

@Composable
fun AboutMoScreen(
    onDismiss: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToOpenSourceStatement: () -> Unit,
    onNavigateToLicense: () -> Unit
) {
    val context = LocalContext.current

    BaseSettingsDialog(title = "关于Mo", onDismiss = onDismiss) { paddingValues ->
        SettingsContentColumn(paddingValues) {
            // Logo和版本信息
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(modifier = Modifier.size(80.dp), color = MaterialTheme.colorScheme.primary, shape = CircleShape) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "M", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
                Text(text = "MoTodo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = "版本 2.0.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "高效工作，简单生活", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            SettingsCard { Column {
                SettingItem(title = "检查更新", description = "当前已是最新版本", icon = Icons.Default.SystemUpdate, onClick = {})
                HorizontalDivider()
                SettingItem(title="开源协议", description="本项目基于开源许可发布", icon=Icons.Default.Code, onClick=onNavigateToLicense)
                HorizontalDivider()
                SettingItem("开源声明", "感谢开源社区的支持", Icons.Default.Favorite, onNavigateToOpenSourceStatement)
            }}

            SettingsCard { Column {
                SettingItem("隐私政策","了解我们如何保护您的数据",Icons.Default.Policy,onNavigateToPrivacyPolicy)
                HorizontalDivider()
                SettingItem("用户协议","使用条款和服务协议",Icons.Default.Description,{})
                HorizontalDivider()
                SettingItem("开源许可证","第三方库的许可证信息",Icons.Default.Gavel,onClick={})
            }}

            Text(text = "© 2024 MoTuT Team. All rights reserved.",
                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth())
        }
    }
}
