package com.motut.mo.ui

import android.Manifest
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.motut.mo.data.Attachment
import com.motut.mo.data.AttachmentType
import com.motut.mo.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemoScreen(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long?, List<Attachment>) -> Unit,
    viewModel: AppViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var attachments by remember { mutableStateOf<List<Attachment>>(emptyList()) }
    
    val context = LocalContext.current
    val categories by viewModel.categories.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileName(context, it)
            val newAttachment = Attachment(
                memoId = 0L,
                type = AttachmentType.IMAGE,
                uri = it.toString(),
                fileName = fileName
            )
            attachments = attachments + newAttachment
        }
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileName(context, it)
            val newAttachment = Attachment(
                memoId = 0L,
                type = AttachmentType.AUDIO,
                uri = it.toString(),
                fileName = fileName
            )
            attachments = attachments + newAttachment
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileName(context, it)
            val newAttachment = Attachment(
                memoId = 0L,
                type = AttachmentType.FILE,
                uri = it.toString(),
                fileName = fileName
            )
            attachments = attachments + newAttachment
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    if (showCategoryPicker) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showCategoryPicker = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "选择分类",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = { 
                            selectedCategoryId = null
                            showCategoryPicker = false 
                        },
                        label = { Text("无分类") },
                        leadingIcon = {
                            Icon(Icons.Default.LabelOff, contentDescription = null)
                        }
                    )
                    
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = { 
                                selectedCategoryId = category.id
                                showCategoryPicker = false 
                            },
                            label = { Text(category.name) },
                            leadingIcon = {
                                Surface(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(category.color),
                                    shape = CircleShape
                                ) {}
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showCategoryPicker = false }) {
                        Text("取消")
                    }
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "关闭",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    "新建备忘录",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.size(48.dp))
            }
            
            FilterChip(
                selected = false,
                onClick = { showCategoryPicker = true },
                label = { 
                    val catName = categories.find { it.id == selectedCategoryId }?.name ?: "无分类"
                    Text(catName)
                },
                leadingIcon = {
                    val catColor = categories.find { it.id == selectedCategoryId }?.color?.let { Color(it) } 
                        ?: MaterialTheme.colorScheme.onSurfaceVariant
                    Surface(
                        modifier = Modifier.size(16.dp),
                        color = catColor,
                        shape = CircleShape
                    ) {}
                }
            )
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Title,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        BasicTextField(
                            value = title,
                            onValueChange = { title = it },
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (title.isEmpty()) {
                                    Text(
                                        "请输入标题",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        fontSize = 18.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }
            }

            BasicTextField(
                value = content,
                onValueChange = { content = it },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp,
                    lineHeight = 28.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                decorationBox = { innerTextField ->
                    if (content.isEmpty()) {
                        Text(
                            "开始记录你的想法...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = 17.sp
                        )
                    }
                    innerTextField()
                }
            )

            if (attachments.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "附件",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    attachments.forEach { attachment ->
                        AttachmentItem(
                            attachment = attachment,
                            onRemove = {
                                attachments = attachments - attachment
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                        imagePickerLauncher.launch("image/*")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "添加图片",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                        }
                        audioPickerLauncher.launch("audio/*")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "添加语音",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = {
                        filePickerLauncher.launch("*/*")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "添加文件",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("取消", color = MaterialTheme.colorScheme.primary)
                }
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onConfirm(title, content, selectedCategoryId, attachments)
                        }
                    },
                    enabled = title.isNotBlank(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("完成")
                }
            }
        }
    }
}

@Composable
fun AttachmentItem(
    attachment: Attachment,
    onRemove: () -> Unit
) {
    val icon = when (attachment.type) {
        AttachmentType.IMAGE -> Icons.Default.Image
        AttachmentType.AUDIO -> Icons.Default.Mic
        AttachmentType.FILE -> Icons.Default.AttachFile
    }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = attachment.fileName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

fun getFileName(context: android.content.Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    result = it.getString(nameIndex)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            result = result?.substring(cut!! + 1)
        }
    }
    return result ?: "unknown"
}
