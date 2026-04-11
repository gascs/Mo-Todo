package com.motut.mo.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.motut.mo.data.ThemeMode
import kotlinx.coroutines.launch

/**
 * 通知设置页面 - 使用 BaseSettingsDialog + SettingCard 组件重构
 * 解决 P1-5: 消除重复的 Dialog/Scaffold/Column 模板代码
 */
@Composable
fun NotificationSettingsScreen(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = remember { (context.applicationContext as com.motut.mo.MoApplication).userPreferences }
    val notificationsEnabled by userPreferences.notificationsEnabled.collectAsState(initial = true)
    val reminderTimeMinutes by userPreferences.notificationReminderTime.collectAsState(initial = 10)

    var reminderEnabled by remember { mutableStateOf(true) }
    var showReminderTimeDialog by remember { mutableStateOf(false) }
    val reminderOptions = listOf(5, 10, 15, 30, 60)

    if (showReminderTimeDialog) {
        AlertDialog(onDismissRequest = { showReminderTimeDialog = false }, title = { Text("选择提醒时间") }, text = {
            Column {
                reminderOptions.forEach { minutes ->
                    Surface(onClick = {
                        scope.launch { userPreferences.setNotificationReminderTime(minutes) }
                        showReminderTimeDialog = false
                    }, modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            RadioButton(selected = reminderTimeMinutes == minutes, onClick = {
                                scope.launch { userPreferences.setNotificationReminderTime(minutes) }
                                showReminderTimeDialog = false
                            })
                            Text("${minutes}分钟前")
                        }
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = { showReminderTimeDialog = false }) { Text("取消") } })
    }

    // 使用统一的 BaseSettingsDialog
    BaseSettingsDialog(title = "通知设置", onDismiss = onDismiss) { paddingValues ->
        SettingsContentColumn(paddingValues) {
            SettingsCard { Column {
                SettingSwitchItem(title = "启用通知", description = "接收任务提醒和更新通知",
                    checked = notificationsEnabled, onCheckedChange = {
                        scope.launch { userPreferences.setNotificationsEnabled(it) }
                    })
                if (notificationsEnabled) {
                    HorizontalDivider()
                    SettingSwitchItem(title = "任务提醒", description = "在任务到期前发送提醒",
                        checked = reminderEnabled, onCheckedChange = { reminderEnabled = it })
                    if (reminderEnabled) {
                        HorizontalDivider()
                        SettingItem(title = "提醒时间", description = "${reminderTimeMinutes}分钟前",
                            onClick = { showReminderTimeDialog = true })
                    }
                }
            }}
        }
    }
}
