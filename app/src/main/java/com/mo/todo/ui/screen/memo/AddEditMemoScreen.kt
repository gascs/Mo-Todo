﻿package com.mo.todo.ui.screen.memo

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import compose.icons.Octicons
import compose.icons.octicons.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StrikethroughS
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.data.model.Memo
import com.mo.todo.data.model.TagConfig
import com.mo.todo.ui.theme.MemoChipColors
import com.mo.todo.ui.viewmodel.MemoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMemoScreen(
    memoId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: MemoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("note") }
    var selectedColor by remember { mutableStateOf(MemoChipColors.first().toArgb()) }

    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    var isStrikethrough by remember { mutableStateOf(false) }
    var isNumberedList by remember { mutableStateOf(false) }

    val isEditing = memoId != null

    LaunchedEffect(memoId) {
        if (memoId != null && memoId > 0) {
            val existing = viewModel.getMemoById(memoId)
            if (existing != null) {
                title = existing.title; content = existing.content
                selectedTag = existing.tag
                existing.color?.let { selectedColor = it }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "编辑备忘录" else "新建备忘录", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Octicons.ArrowLeft24, "返回") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
            Spacer(Modifier.height(8.dp))
            Text("标题", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("输入备忘录标题...") }, singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant, focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.small)

            Spacer(Modifier.height(20.dp))
            Text("内容", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(value = content, onValueChange = { content = it },
                modifier = Modifier.fillMaxWidth().height(180.dp),
                placeholder = { Text("开始记录...") }, maxLines = 10,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant, focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.small)

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple(Octicons.Bold24, "加粗") { isBold = !isBold; content = if (isBold) "**${content}**" else content },
                    Triple(Octicons.Italic24, "斜体") { isItalic = !isItalic; content = if (isItalic) "*${content}*" else content },
                    Triple(Icons.Filled.StrikethroughS, "删除线") { isStrikethrough = !isStrikethrough; content = if (isStrikethrough) "~~${content}~~" else content },
                    Triple(Octicons.ListUnordered24, "无序列表") { content += "\n- " },
                    Triple(Octicons.ListOrdered24, "有序列表") { content += "\n1. " }
                ).forEach { (icon, desc, action) ->
                    Box(Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant).clickable { action() }, contentAlignment = Alignment.Center) {
                        Icon(icon, desc, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("颜色标签", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MemoChipColors.forEach { color ->
                    val isSelected = selectedColor == color.toArgb()
                    Box(Modifier.size(32.dp).clip(CircleShape).background(color).clickable { selectedColor = color.toArgb() }, contentAlignment = Alignment.Center) {
                        if (isSelected) Box(Modifier.size(10.dp).clip(CircleShape).background(Color.White))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("分类", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagConfig.memoTags.filter { it.key != "all" }.forEach { tag ->
                    val s = selectedTag == tag.key
                    Box(Modifier.clip(MaterialTheme.shapes.small).background(if (s) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant).clickable { selectedTag = tag.key }.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(tag.label, style = MaterialTheme.typography.labelLarge, color = if (s) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = if (s) FontWeight.SemiBold else FontWeight.Normal)
                    }
                }
            }

            Spacer(Modifier.height(36.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onNavigateBack, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.small) { Text("取消") }
                Button(onClick = {
                    if (title.isBlank()) { Toast.makeText(context, "请输入标题", Toast.LENGTH_SHORT).show(); return@Button }
                    val memo = Memo(id = memoId ?: 0, title = title, content = content, tag = selectedTag, color = selectedColor, updatedAt = System.currentTimeMillis())
                    if (isEditing) viewModel.updateMemo(memo) else viewModel.insertMemo(memo)
                    onNavigateBack()
                }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), shape = MaterialTheme.shapes.small) { Text("保存") }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
