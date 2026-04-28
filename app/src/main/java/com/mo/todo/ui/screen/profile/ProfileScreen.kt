package com.mo.todo.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.ui.viewmodel.SettingsViewModel
import com.mo.todo.ui.viewmodel.ThemeMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val scope = rememberCoroutineScope()
    var showThemeDialog by remember { mutableStateOf(false) }

    val themeLabel = when (themeMode) {
        ThemeMode.SYSTEM -> "跟随系统"
        ThemeMode.LIGHT -> "浅色"
        ThemeMode.DARK -> "深色"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mo \u00b7 我的",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Color(0xFF7CA88B)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Mo 用户",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "让生活井井有条",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.height(8.dp))

            // Settings items
        SettingsItem(
            icon = Icons.Filled.Label,
                iconBackground = Color(0xFFE8F5E9),
                iconTint = Color(0xFF388E3C),
                label = "标签管理",
                subtitle = "4个标签",
                onClick = { }
            )

        SettingsItem(
            icon = Icons.Filled.Notifications,
                iconBackground = Color(0xFFFFF3E0),
                iconTint = Color(0xFFF57C00),
                label = "提醒默认设置",
                subtitle = "提前10分钟",
                onClick = { }
            )

            SettingsItem(
                icon = if (themeMode == ThemeMode.DARK) Icons.Filled.DarkMode else Icons.Filled.BrightnessHigh,
                iconBackground = Color(0xFFE3F2FD),
                iconTint = Color(0xFF1976D2),
                label = "主题",
                subtitle = themeLabel,
                onClick = {
                    // Cycle through themes
                    scope.launch {
                        when (themeMode) {
                            ThemeMode.SYSTEM -> viewModel.setThemeMode(ThemeMode.LIGHT)
                            ThemeMode.LIGHT -> viewModel.setThemeMode(ThemeMode.DARK)
                            ThemeMode.DARK -> viewModel.setThemeMode(ThemeMode.SYSTEM)
                        }
                    }
                }
            )

            SettingsItem(
                icon = Icons.Filled.SaveAlt,
                iconBackground = Color(0xFFFCE4EC),
                iconTint = Color(0xFFC62828),
                label = "数据备份与导出",
                subtitle = "未备份",
                onClick = { }
            )

            SettingsItem(
                icon = Icons.Filled.Info,
                iconBackground = Color(0xFFF3E5F5),
                iconTint = Color(0xFF7B1FA2),
                label = "关于 Mo",
                subtitle = "v1.0.0",
                onClick = { }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    label: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 4.dp)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
        )
    }
}
