package com.motut.mo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.LabelOff
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.motut.mo.data.Memo
import com.motut.mo.data.MemoCategory
import com.motut.mo.viewmodel.AppViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoDetailScreen(
    memo: Memo,
    onDismiss: () -> Unit,
    onSave: (Long, String, String, Long?) -> Unit,
    onTogglePin: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    viewModel: AppViewModel = viewModel()
) {
    var title by remember { mutableStateOf(memo.title) }
    var content by remember { mutableStateOf(memo.content) }
    var selectedCategoryId by remember { mutableStateOf(memo.categoryId) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    val categories by viewModel.categories.collectAsState()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm") }
    
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
                            Icon(Icons.AutoMirrored.Filled.LabelOff, contentDescription = null)
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
    
    if (showDeleteConfirm) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showDeleteConfirm = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "确认删除",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "确定要删除这条备忘录吗？此操作无法撤销。",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDeleteConfirm = false }) {
                            Text("取消")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onDelete(memo.id)
                                showDeleteConfirm = false
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("删除")
                        }
                    }
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑备忘录") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { onTogglePin(memo.id) }) {
                        Icon(
                            imageVector = if (memo.isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                            contentDescription = if (memo.isPinned) "取消置顶" else "置顶",
                            tint = if (memo.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    IconButton(
                        onClick = { onSave(memo.id, title, content, selectedCategoryId) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "保存",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "创建于: ${memo.createdAt.format(dateFormatter)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (memo.updatedAt != memo.createdAt) {
                        Text(
                            text = " · 更新于: ${memo.updatedAt.format(dateFormatter)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
            
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        Text(
                            text = "输入标题",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    innerTextField()
                }
            )
            
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
                            text = "开始记录...",
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                fontSize = 17.sp
                            )
                        )
                    }
                    innerTextField()
                }
            )
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
