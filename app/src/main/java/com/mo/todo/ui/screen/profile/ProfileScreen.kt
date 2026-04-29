﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿package com.mo.todo.ui.screen.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import compose.icons.Octicons
import compose.icons.octicons.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mo.todo.ui.theme.MoPrimary
import com.mo.todo.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLabelManagement: () -> Unit = {},
    onNavigateToReminderSettings: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToPersonalization: () -> Unit = {},
    onNavigateToDataManagement: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val nickname by viewModel.nickname.collectAsState()
    val avatarPath by viewModel.avatarPath.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
            title = { Text("修改昵称") },
            text = {
                OutlinedTextField(
                    value = editNick, onValueChange = { editNick = it },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant),
                    shape = MaterialTheme.shapes.small
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
            TopAppBar(title = { Text("Mo · 我的", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())) {
            Row(Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(64.dp).clip(CircleShape).background(MoPrimary).clickable { avatarLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center) {
                    if (avatarPath.isNotBlank()) {
                        AsyncImage(model = File(avatarPath), contentDescription = "头像", modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
                    } else {
                        Text("M", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.clickable { editNick = nickname; showNickDialog = true }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(nickname, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.width(6.dp))
                        Icon(Octicons.Pencil24, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                    }
                    Spacer(Modifier.height(2.dp))
                    Text("让生活井井有条", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(Modifier.height(8.dp))

            ProfileMenuItem(Octicons.Tag24, Color(0xFF0969DA), "标签管理", "管理待办和备忘标签", onClick = onNavigateToLabelManagement)
            ProfileMenuItem(Octicons.Bell24, Color(0xFFBF8700), "提醒默认设置", "设置默认提醒时间", onClick = onNavigateToReminderSettings)
            ProfileMenuItem(Icons.Filled.Palette, Color(0xFF8250DF), "个性化", "配色方案 / 字体 / 圆角 / 主题", onClick = onNavigateToPersonalization)
            ProfileMenuItem(Octicons.Database24, Color(0xFFCF222E), "数据管理", "本地导出导入 / WebDAV 备份恢复", onClick = onNavigateToDataManagement)
            ProfileMenuItem(Octicons.Info24, Color(0xFF0969DA), "关于 Mo", "v1.0.0", onClick = onNavigateToAbout)

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, iconTint: Color, label: String, subtitle: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(44.dp).clip(CircleShape).background(iconTint.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp)) }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Octicons.ChevronRight24, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
    }
}
