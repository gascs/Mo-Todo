package com.mo.todo.ui.screen.memo

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.data.model.Memo
import com.mo.todo.ui.viewmodel.MemoViewModel

val memoColorOptions = listOf(
    Color(0xFFA5D6A7),
    Color(0xFFFFCC80),
    Color(0xFF90CAF9),
    Color(0xFFF48FB1),
    Color(0xFFCE93D8),
    Color(0xFFFFF9C4),
    Color(0xFFD7CCC8)
)

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
    var selectedColor by remember { mutableStateOf(memoColorOptions.first().toArgb()) }

    val isEditing = memoId != null

    LaunchedEffect(memoId) {
        if (memoId != null && memoId > 0) {
            val existing = viewModel.getMemoById(memoId)
            if (existing != null) {
                title = existing.title
                content = existing.content
                selectedTag = existing.tag
                existing.color?.let { selectedColor = it }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "编辑备忘录" else "新建备忘录",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "标题",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("输入备忘录标题...") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "内容",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                placeholder = { Text("开始记录...") },
                maxLines = 10
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Format toolbar placeholder
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { i ->
                    val icons = listOf("B", "I", "U", "≡")
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .clickable { },
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = icons[i],
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "颜色标签",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                memoColorOptions.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .clickable { selectedColor = color.toArgb() }
                            .then(
                                if (selectedColor == color.toArgb()) {
                                    Modifier.padding(0.dp)
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        if (selectedColor == color.toArgb()) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .align(androidx.compose.ui.Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "分类",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                memoTags.filter { it.first != "all" }.forEach { (key, label) ->
                    FilterChip(
                        selected = selectedTag == key,
                        onClick = { selectedTag = key },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("取消")
                }
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            Toast.makeText(context, "请输入标题", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val memo = Memo(
                            id = memoId ?: 0,
                            title = title,
                            content = content,
                            tag = selectedTag,
                            color = selectedColor,
                            updatedAt = System.currentTimeMillis()
                        )
                        if (isEditing) {
                            viewModel.updateMemo(memo)
                        } else {
                            viewModel.insertMemo(memo)
                        }
                        onNavigateBack()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("保存")
                }
            }
        }
    }
}
