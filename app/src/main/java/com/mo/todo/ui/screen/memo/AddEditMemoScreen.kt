package com.mo.todo.ui.screen.memo

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import compose.icons.Octicons
import compose.icons.octicons.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.R
import com.mo.todo.data.model.Memo
import com.mo.todo.data.model.TagConfig
import com.mo.todo.ui.viewmodel.MemoViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditMemoScreen(
    memoId: Long?,
    onNavigateBack: () -> Unit,
    memoViewModel: MemoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("便签") }
    var selectedColorIndex by remember { mutableStateOf(0) }
    val isEditing = memoId != null
    val colorNames = listOf("默认", "薰衣草", "薄荷", "珊瑚", "琥珀", "天空", "玫瑰")

    LaunchedEffect(memoId) {
        if (memoId != null && memoId > 0) {
            val existing = memoViewModel.getMemoById(memoId)
            if (existing != null) {
                title = existing.title
                content = existing.content
                selectedTag = existing.tag
                selectedColorIndex = existing.color ?: 0
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(if (isEditing) R.string.memo_edit_title else R.string.memo_create_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Octicons.ArrowLeft24, stringResource(R.string.btn_cancel))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                stringResource(R.string.memo_field_title),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.memo_field_title_placeholder)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.small
            )

            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(R.string.memo_field_content),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                placeholder = { Text(stringResource(R.string.memo_field_content_placeholder)) },
                maxLines = 8,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.small
            )

            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(R.string.memo_field_tag),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagConfig.memoTags.filter { it.key != "all" }.forEach { tag ->
                    val s = selectedTag == tag.label
                    val resId = TagConfig.displayNameResId(tag.key)
                    val displayName = if (resId != 0) context.getString(resId) else tag.label
                    Box(
                        Modifier
                            .padding(bottom = 4.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(if (s) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedTag = tag.label }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            displayName,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (s) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (s) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(R.string.memo_field_color),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                colorNames.forEachIndexed { index, c ->
                    val s = selectedColorIndex == index
                    val colorResId = TagConfig.memoColorDisplayNameResId(c)
                    val colorDisplayName = if (colorResId != 0) stringResource(colorResId) else c
                    Box(
                        Modifier
                            .padding(bottom = 4.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(if (s) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedColorIndex = index }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            colorDisplayName,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (s) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (s) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(Modifier.height(36.dp))

            Button(
                onClick = {
                    if (title.isBlank()) {
                        Toast.makeText(context, context.getString(R.string.toast_title_empty), Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val memo = Memo(
                        id = if (isEditing) memoId!! else 0,
                        title = title,
                        content = content,
                        tag = selectedTag,
                        color = selectedColorIndex,
                        updatedAt = System.currentTimeMillis()
                    )
                    if (isEditing) memoViewModel.updateMemo(memo) else memoViewModel.insertMemo(memo)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.small
            ) {
                Text(stringResource(if (isEditing) R.string.memo_btn_save else R.string.memo_btn_create))
            }

            if (isEditing) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        memoViewModel.deleteMemoById(memoId!!)
                        Toast.makeText(context, context.getString(R.string.toast_deleted), Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(Octicons.Trash24, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.memo_btn_delete))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
