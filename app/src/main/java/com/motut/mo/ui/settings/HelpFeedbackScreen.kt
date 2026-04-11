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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun HelpFeedbackScreen(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var feedbackText by remember { mutableStateOf("") }
    var feedbackSent by remember { mutableStateOf(false) }

    BaseSettingsDialog(title = "帮助与反馈", onDismiss = onDismiss) { paddingValues ->
        SettingsContentColumn(paddingValues) {
            // 常见问题
            SettingsCard { Column {
                Text(text = "常见问题", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                SettingItem(title = "如何添加待办？", description = "点击右下角 + 按钮，选择新建任务",
                    icon = Icons.Default.HelpOutline, onClick = {})
                HorizontalDivider()
                SettingItem(title = "如何设置提醒？", description = "在任务详情中可设置日期和时间",
                    icon = Icons.Default.Notifications, onClick = {})
                HorizontalDivider()
                SettingItem(title = "如何备份数据？", description = "前往 设置 > 数据备份 进行操作",
                    icon = Icons.Default.Backup, onClick = {})
                HorizontalDivider()
                SettingItem(title = "如何使用日历？", description = "点击底部 日历 Tab 查看任务分布",
                    icon = Icons.Default.CalendarToday, onClick = {})
            }}

            // 联系我们
            SettingsCard { Column {
                Text(text = "联系我们", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(16.dp)) {
                    FilledTonalButton(onClick = { sendEmail(context, "support@motut.app", subject = "MoTodo 反馈") }) {
                        Icon(Icons.Default.Mail, null); Spacer(Modifier.width(8.dp)); Text("邮件反馈")
                    }
                    FilledTonalButton(onClick = { openUrl(context, "https://github.com/motut/mo-todo/issues") }) {
                        Icon(Icons.Default.BugReport, null); Spacer(Modifier.width(8.dp)); Text("提交Issue")
                    }
                }

                if (feedbackSent) {
                    Surface(modifier = Modifier.fillMaxWidth().padding(16.dp),
                        color = com.motut.mo.ui.theme.AppColors.SuccessModern.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)) {
                        Text(text = "感谢您的反馈！我们会尽快处理。", color = com.motut.mo.ui.theme.AppColors.SuccessModern)
                    }
                } else {
                    OutlinedTextField(value = feedbackText, onValueChange = { feedbackText = it },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        label = { Text("输入您的建议或问题...") }, minLines = 3,
                        shape = MaterialTheme.shapes.medium)
                    Button(onClick = {
                        if (feedbackText.isNotBlank()) {
                            scope.launch { /* TODO: 提交反馈到服务器 */ }
                            feedbackSent = true
                        }
                    }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
                        Icon(Icons.Default.Send, null); Spacer(Modifier.width(8.dp)); Text("提交反馈")
                    }
                }
            }}
        }
    }
}
