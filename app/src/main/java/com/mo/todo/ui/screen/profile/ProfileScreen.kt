package com.mo.todo.ui.screen.profile

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import compose.icons.Octicons
import compose.icons.octicons.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mo.todo.ui.viewmodel.SettingsViewModel
import com.mo.todo.ui.viewmodel.StatsViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLabelManagement: () -> Unit = {},
    onNavigateToReminderSettings: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToPersonalization: () -> Unit = {},
    onNavigateToDataManagement: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
    statsViewModel: StatsViewModel = hiltViewModel()
) {
    val nickname by viewModel.nickname.collectAsState()
    val avatarPath by viewModel.avatarPath.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val todoTotal by statsViewModel.todoTotal.collectAsState()
    val todoCompleted by statsViewModel.todoCompleted.collectAsState()
    val todoActive by statsViewModel.todoActive.collectAsState()
    val memoTotal by statsViewModel.memoTotal.collectAsState()
    val memoStarred by statsViewModel.memoStarred.collectAsState()
    val completionRate by statsViewModel.completionRate.collectAsState()

    val greeting = remember {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "早上好"
            in 12..13 -> "中午好"
            in 14..17 -> "下午好"
            in 18..22 -> "晚上好"
            else -> "夜深了"
        }
    }

    var showNickDialog by remember { mutableStateOf(false) }
    var editNick by remember { mutableStateOf(nickname) }

    val avatarLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val file = File(context.filesDir, "avatar_${System.currentTimeMillis()}.jpg")
                inputStream?.use { inStream -> file.outputStream().use { outStream -> inStream.copyTo(outStream) } }
                scope.launch { viewModel.setAvatarPath(file.absolutePath) }
            } catch (e: Exception) {
                Toast.makeText(context, "头像保存失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showNickDialog) {
        AlertDialog(
            onDismissRequest = { showNickDialog = false },
            title = { Text("修改昵称", style = MaterialTheme.typography.titleMedium) },
            text = {
                OutlinedTextField(
                    value = editNick, onValueChange = { editNick = it },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = MaterialTheme.shapes.medium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { viewModel.setNickname(editNick.ifBlank { "Mo 用户" }) }
                    showNickDialog = false
                }) { Text("确定") }
            },
            dismissButton = { TextButton(onClick = { showNickDialog = false }) { Text("取消") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "我的",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { editNick = nickname; showNickDialog = true }
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { avatarLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarPath.isNotBlank()) {
                        AsyncImage(
                            model = File(avatarPath),
                            contentDescription = "头像",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            nickname.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        "$greeting，$nickname",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "让生活井井有条",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "数据概览",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "完成率 $completionRate%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { if (todoTotal == 0) 0f else todoCompleted.toFloat() / todoTotal },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatItem("待办", todoTotal.toString(), MaterialTheme.colorScheme.primary)
                        StatItem("进行中", todoActive.toString(), Color(0xFFBF8700))
                        StatItem("已完成", todoCompleted.toString(), Color(0xFF1A7F37))
                        StatItem("备忘录", memoTotal.toString(), Color(0xFF8250DF))
                        StatItem("收藏", memoStarred.toString(), Color(0xFFCF222E))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            SectionHeader("功能")
            ProfileMenuItem(
                Octicons.Tag24,
                MaterialTheme.colorScheme.primary,
                "标签管理",
                "管理待办和备忘标签",
                onClick = onNavigateToLabelManagement
            )
            ProfileMenuItem(
                Octicons.Bell24,
                Color(0xFFBF8700),
                "提醒默认设置",
                "设置默认提醒时间",
                onClick = onNavigateToReminderSettings
            )
            ProfileMenuItem(
                Icons.Filled.Palette,
                Color(0xFF8250DF),
                "个性化",
                "配色 / 字体 / 圆角 / 主题",
                onClick = onNavigateToPersonalization
            )
            ProfileMenuItem(
                Octicons.Database24,
                Color(0xFFCF222E),
                "数据管理",
                "导出导入 / WebDAV 备份",
                onClick = onNavigateToDataManagement
            )

            Spacer(Modifier.height(12.dp))
            SectionHeader("其他")
            ProfileMenuItem(
                Icons.Filled.Share,
                Color(0xFF1A7F37),
                "分享给朋友",
                "推荐 Mo 给身边的人",
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "推荐一个简洁好用的待办备忘 App —— Mo\nhttps://github.com/gascs/Mo-Todo")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "分享 Mo"))
                }
            )
            ProfileMenuItem(
                Octicons.Info24,
                MaterialTheme.colorScheme.onSurfaceVariant,
                "关于 Mo",
                "v1.0.0",
                onClick = onNavigateToAbout
            )

            Spacer(Modifier.height(112.dp))
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
        Icon(
            Octicons.ChevronRight24,
            null,
            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            modifier = Modifier.size(18.dp)
        )
    }
}
