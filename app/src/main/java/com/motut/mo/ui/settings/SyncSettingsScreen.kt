package com.motut.mo.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SyncSettingsScreen(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = remember { (context.applicationContext as com.motut.mo.MoApplication).userPreferences }
    val customSyncSource by userPreferences.customSyncSource.collectAsState(initial = "")
    var autoSync by remember { mutableStateOf(true) }
    var syncOnlyOnWifi by remember { mutableStateOf(false) }
    var syncSource by remember { mutableStateOf(if (customSyncSource.isEmpty()) "本地" else "自定义") }
    var saveFormat by remember { mutableStateOf("JSON") }
    var showSyncSourceDialog by remember { mutableStateOf(false) }
    var showSaveFormatDialog by remember { mutableStateOf(false) }
    var showCustomSourceDialog by remember { mutableStateOf(false) }
    var customSourceInput by remember { mutableStateOf(customSyncSource) }

    if (showSyncSourceDialog) {
        AlertDialog(onDismissRequest = { showSyncSourceDialog = false }, title = { Text("选择同步源") }, text = {
            Column {
                listOf("本地", "Google Drive", "WebDAV", "OneDrive", "自定义").forEach { source ->
                    Surface(onClick = {
                        if (source == "自定义") { showSyncSourceDialog = false; showCustomSourceDialog = true }
                        else { syncSource = source; scope.launch { userPreferences.setCustomSyncSource("") }; showSyncSourceDialog = false }
                    }, modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            RadioButton(selected = syncSource == source || (source == "自定义" && customSyncSource.isNotEmpty()), onClick = {
                                if (source == "自定义") { showSyncSourceDialog = false; showCustomSourceDialog = true }
                                else { syncSource = source; scope.launch { userPreferences.setCustomSyncSource("") }; showSyncSourceDialog = false }
                            })
                            Text(source)
                        }
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = { showSyncSourceDialog = false }) { Text("取消") } })
    }

    if (showCustomSourceDialog) {
        AlertDialog(onDismissRequest = { showCustomSourceDialog = false }, title = { Text("自定义同步源") }, text = {
            Column {
                OutlinedTextField(value = customSourceInput, onValueChange = { customSourceInput = it },
                    modifier = Modifier.fillMaxWidth(), label = { Text("输入同步源地址") },
                    placeholder = { Text("https://example.com/sync") })
            }
        }, confirmButton = { TextButton(onClick = {
            scope.launch { userPreferences.setCustomSyncSource(customSourceInput) }
            syncSource = if (customSourceInput.isNotEmpty()) "自定义" else "本地"; showCustomSourceDialog = false
        }) { Text("确定") } }, dismissButton = { TextButton(onClick = { showCustomSourceDialog = false }) { Text("取消") } })
    }

    if (showSaveFormatDialog) {
        AlertDialog(onDismissRequest = { showSaveFormatDialog = false }, title = { Text("选择保存格式") }, text = {
            Column { listOf("JSON", "CSV", "XML").forEach { format ->
                Surface(onClick = { saveFormat = format; showSaveFormatDialog = false },
                    modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RadioButton(selected = saveFormat == format, onClick = { saveFormat = format; showSaveFormatDialog = false })
                        Text(format)
                    }
                }
            }}
        }, confirmButton = { TextButton(onClick = { showSaveFormatDialog = false }) { Text("取消") } })
    }

    BaseSettingsDialog(title = "数据同步", onDismiss = onDismiss) { paddingValues ->
        SettingsContentColumn(paddingValues) {
            SettingsCard { Column {
                SettingSwitchItem(title = "自动同步", description = "自动同步数据到云端",
                    checked = autoSync, onCheckedChange = { autoSync = it })
                if (autoSync) { HorizontalDivider()
                    SettingSwitchItem(title = "仅WiFi同步", description = "仅在WiFi网络下进行同步",
                        checked = syncOnlyOnWifi, onCheckedChange = { syncOnlyOnWifi = it })
                }
            }}
            SettingsCard { Column {
                SettingItem(title = "同步源", description = if (customSyncSource.isNotEmpty()) customSyncSource else syncSource,
                    icon = Icons.Default.CloudSync, onClick = { showSyncSourceDialog = true })
                HorizontalDivider()
                SettingItem(title = "保存格式", description = saveFormat, icon = Icons.Default.Description,
                    onClick = { showSaveFormatDialog = true })
            }}
            Button(onClick = {}, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
                Icon(Icons.Default.Refresh, null); Spacer(Modifier.width(8.dp)); Text("立即同步")
            }
        }
    }
}
