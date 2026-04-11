package com.motut.mo.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.motut.mo.data.ThemeMode
import kotlinx.coroutines.launch

@Composable
fun AppearanceSettingsScreen(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = remember { (context.applicationContext as com.motut.mo.MoApplication).userPreferences }
    val themeMode by userPreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM.name)
    val useDynamicColor by userPreferences.useDynamicColor.collectAsState(initial = true)
    val customPrimaryColor by userPreferences.customPrimaryColor.collectAsState(initial = 0)
    val backgroundImageUri by userPreferences.backgroundImageUri.collectAsState(initial = "")
    val currentThemeMode = remember { mutableStateOf(ThemeMode.valueOf(themeMode)) }
    var showColorPicker by remember { mutableStateOf(false) }
    val colorOptions = listOf(0 to "跟随系统取色", 0xFF6650a4.toInt() to "紫色", 0xFFE57373.toInt() to "红色",
        0xFFFFB74D.toInt() to "橙色", 0xFF81C784.toInt() to "绿色", 0xFF64B5F6.toInt() to "蓝色", 0xFFBA68C8.toInt() to "粉色")
    val pickImageLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let { scope.launch { userPreferences.setBackgroundImageUri(it.toString()) } }
    }

    if (showColorPicker) {
        AlertDialog(onDismissRequest = { showColorPicker = false }, title = { Text("选择主题颜色") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                colorOptions.forEach { (color, name) ->
                    Surface(onClick = {
                        scope.launch {
                            if (color == 0) { userPreferences.setUseDynamicColor(true); userPreferences.setCustomPrimaryColor(0) }
                            else { userPreferences.setUseDynamicColor(false); userPreferences.setCustomPrimaryColor(color) }
                        }
                        showColorPicker = false
                    }, modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (color != 0) Surface(modifier = Modifier.size(24.dp), color = Color(color), shape = CircleShape) {}
                            else Surface(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary, shape = CircleShape) {}
                            Text(name)
                            if ((color == 0 && useDynamicColor) || (color != 0 && customPrimaryColor == color && !useDynamicColor)) {
                                Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = { showColorPicker = false }) { Text("取消") } })
    }

    BaseSettingsDialog(title = "外观主题", onDismiss = onDismiss) { paddingValues ->
        SettingsContentColumn(paddingValues) {
            SettingsCard { Column {
                ThemeOptionItem(title = "跟随系统", description = "自动跟随系统设置",
                    selected = currentThemeMode.value == ThemeMode.SYSTEM, onClick = {
                        currentThemeMode.value = ThemeMode.SYSTEM; scope.launch { userPreferences.setThemeMode(ThemeMode.SYSTEM) }
                    })
                HorizontalDivider()
                ThemeOptionItem(title = "浅色模式", description = "始终使用浅色主题",
                    selected = currentThemeMode.value == ThemeMode.LIGHT, onClick = {
                        currentThemeMode.value = ThemeMode.LIGHT; scope.launch { userPreferences.setThemeMode(ThemeMode.LIGHT) }
                    })
                HorizontalDivider()
                ThemeOptionItem(title = "深色模式", description = "始终使用深色主题",
                    selected = currentThemeMode.value == ThemeMode.DARK, onClick = {
                        currentThemeMode.value = ThemeMode.DARK; scope.launch { userPreferences.setThemeMode(ThemeMode.DARK) }
                    })
            }}
            SettingsCard { Column {
                SettingItem(title = "主题颜色", description = if (useDynamicColor) "跟随系统取色" else "自定义颜色",
                    icon = Icons.Default.Palette, onClick = { showColorPicker = true })
                HorizontalDivider()
                SettingItem(title = "背景图片", description = if (backgroundImageUri.isNotEmpty()) "已设置" else "未设置",
                    icon = Icons.Default.Image, onClick = { pickImageLauncher.launch("image/*") })
                if (backgroundImageUri.isNotEmpty()) {
                    HorizontalDivider()
                    SettingItem(title = "清除背景", description = "恢复默认背景", icon = Icons.Default.Delete,
                        onClick = { scope.launch { userPreferences.setBackgroundImageUri("") } })
                }
            }}
        }
    }
}

@Composable
fun ThemeOptionItem(title: String, description: String, selected: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(text = description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (selected) Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}
