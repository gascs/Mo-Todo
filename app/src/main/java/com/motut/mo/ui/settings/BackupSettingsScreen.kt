package com.motut.mo.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.motut.mo.data.DataBackupManager
import com.motut.mo.viewmodel.AppViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BackupSettingsScreen(
    viewModel: AppViewModel,
    onDismiss: () -> Unit,
    onSnackbar: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isBackingUp by remember { mutableStateOf(false) }
    var isRestoring by remember { mutableStateOf(false) }
    var lastBackupTime by remember { mutableStateOf<String?>(null) }

    // 检查上次备份时间
    LaunchedEffect(Unit) {
        try {
            val backupDir = File(context.filesDir, "backups")
            if (backupDir.exists()) {
                val files = backupDir.listFiles { it.extension == "db" }
                files?.maxByOrNull { it.lastModified() }?.let {
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    lastBackupTime = sdf.format(Date(it.lastModified()))
                }
            }
        } catch (e: Exception) { com.motut.mo.util.AppLog.e("Backup", "检查备份失败", e) }
    }

    fun performBackup() {
        scope.launch {
            isBackingUp = true
            try {
                val backupDir = File(context.filesDir, "backups").also { it.mkdirs() }
                val dbFile = context.getDatabasePath("motodo_db")
                if (dbFile.exists()) {
                    val destFile = File(backupDir, "backup_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.db")
                    dbFile.copyTo(destFile, overwrite = true)
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    lastBackupTime = sdf.format(Date())
                    onSnackbar("备份成功！")
                } else {
                    onSnackbar("未找到数据库文件")
                }
            } catch (e: Exception) {
                com.motut.mo.util.AppLog.e("Backup", "备份失败", e)
                onSnackbar("备份失败: ${e.message}")
            } finally { isBackingUp = false }
        }
    }

    BaseSettingsDialog(title = "数据备份", onDismiss = onDismiss) { paddingValues ->
        SettingsContentColumn(paddingValues) {
            SettingsCard { Column {
                SettingItem(title = "立即备份", description = if (!isBackingUp)
                    "将所有数据备份到本地存储" else "正在备份中...", icon = Icons.Default.Backup,
                    onClick = { if (!isBackingUp && !isRestoring) performBackup() })
                HorizontalDivider()
                SettingItem(title = "恢复数据", description = "从备份文件恢复数据（即将支持）",
                    icon = Icons.Default.Restore, onClick = { onSnackbar("此功能开发中...") })
            }}
            SettingsCard { Column {
                SettingItem(title = "自动备份", description = "每周自动备份数据（即将支持）",
                    icon = Icons.Default.Schedule, onClick = { onSnackbar("此功能开发中...") })
            }}
            if (lastBackupTime != null) {
                Surface(modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.History, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(text = "最近备份", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(text = lastBackupTime!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        }
                    }
                }
            }
            Text(text = "提示：备份文件保存在应用私有目录中，卸载应用后将被删除。建议定期导出到安全位置。",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
