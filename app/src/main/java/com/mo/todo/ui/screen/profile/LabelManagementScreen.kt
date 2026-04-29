﻿﻿﻿﻿﻿package com.mo.todo.ui.screen.profile

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import compose.icons.Octicons
import compose.icons.octicons.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

val defaultLabels = listOf("工作", "个人", "购物", "便签", "阅读笔记", "项目")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val customLabels by viewModel.customLabelsState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var newLabelText by remember { mutableStateOf("") }
    var deleteTarget by remember { mutableStateOf<String?>(null) }
    var isBatchMode by remember { mutableStateOf(false) }
    var selectedLabels by remember { mutableStateOf<Set<String>>(emptySet()) }

    val allLabels = (defaultLabels + customLabels.toList()).distinct()

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false; newLabelText = "" },
            title = { Text("添加标签") },
            text = {
                OutlinedTextField(value = newLabelText, onValueChange = { newLabelText = it }, modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("输入标签名称") }, singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant),
                    shape = MaterialTheme.shapes.small)
            },
            confirmButton = {
                TextButton(onClick = {
                    val trimmed = newLabelText.trim()
                    if (trimmed.isBlank()) { Toast.makeText(context, "标签名不能为空", Toast.LENGTH_SHORT).show(); return@TextButton }
                    if (trimmed in allLabels) { Toast.makeText(context, "标签已存在", Toast.LENGTH_SHORT).show(); return@TextButton }
                    scope.launch { viewModel.addCustomLabel(trimmed); newLabelText = ""; showAddDialog = false }
                }) { Text("确定") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false; newLabelText = "" }) { Text("取消") } }
        )
    }

    deleteTarget?.let { label ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("删除标签") },
            text = { Text("确定删除标签「${label}」？使用该标签的待办和备忘将标记为「未分类」。") },
            confirmButton = {
                TextButton(onClick = { scope.launch { viewModel.removeCustomLabel(label); deleteTarget = null; Toast.makeText(context, "已删除", Toast.LENGTH_SHORT).show() } }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("取消") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isBatchMode) "已选 ${selectedLabels.size} 项" else "标签管理", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { if (isBatchMode) { isBatchMode = false; selectedLabels = emptySet() } else onNavigateBack() }) { Icon(Octicons.ArrowLeft24, "返回") } },
                actions = {
                    if (isBatchMode) {
                        IconButton(onClick = {
                            if (selectedLabels.isNotEmpty()) {
                                scope.launch { viewModel.removeCustomLabels(selectedLabels); selectedLabels = emptySet(); isBatchMode = false }
                                Toast.makeText(context, "已删除 ${selectedLabels.size} 个标签", Toast.LENGTH_SHORT).show()
                            }
                        }) { Icon(Octicons.Trash24, "批量删除", tint = MaterialTheme.colorScheme.error) }
                    } else {
                        IconButton(onClick = { showAddDialog = true }) { Icon(Octicons.Plus24, "添加") }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(4.dp))
            allLabels.forEach { label ->
                val isCustom = label in customLabels
                val checked = selectedLabels.contains(label)
                Card(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 3.dp)
                        .clickable {
                            if (isBatchMode && isCustom) {
                                selectedLabels = if (checked) selectedLabels - label else selectedLabels + label
                            } else if (!isBatchMode && isCustom) {
                                isBatchMode = true; selectedLabels = setOf(label)
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = if (checked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (isBatchMode && isCustom) { Checkbox(checked = checked, onCheckedChange = { selectedLabels = if (it) selectedLabels + label else selectedLabels - label }); Spacer(Modifier.width(4.dp)) }
                        Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                        Text(if (isCustom) "自定义" else "默认", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (!isBatchMode && isCustom) {
                            Spacer(Modifier.width(10.dp))
                            IconButton(onClick = { deleteTarget = label }, modifier = Modifier.size(32.dp)) { Icon(Octicons.X24, "删除", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(18.dp)) }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
