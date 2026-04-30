package com.mo.todo.ui.screen.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.R
import com.mo.todo.data.model.TagConfig
import com.mo.todo.ui.viewmodel.TodoViewModel
import com.mo.todo.ui.viewmodel.MemoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LabelManagementScreen(
    onNavigateBack: () -> Unit,
    todoViewModel: TodoViewModel = hiltViewModel(),
    memoViewModel: MemoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val activeTodos by todoViewModel.activeTodos.collectAsState()
    val completedTodos by todoViewModel.completedTodos.collectAsState()
    val allTodos = activeTodos + completedTodos
    val allMemos by memoViewModel.memos.collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var newLabel by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var showRenameDialog by remember { mutableStateOf<String?>(null) }
    var renameText by remember { mutableStateOf("") }

    val defaultLabels = listOf("工作", "个人", "购物", "便签", "阅读笔记", "项目")
    val usedLabels = (allTodos.map { it.tag } + allMemos.map { it.tag }).distinct()
    val allLabels = (defaultLabels + usedLabels).distinct().filter { it.isNotBlank() }.sorted()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.label_manage_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Octicons.ArrowLeft24, stringResource(R.string.btn_back))
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Octicons.Plus24, stringResource(R.string.label_manage_btn_add))
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Octicons.Tag16, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            stringResource(R.string.label_manage_stats_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            stringResource(R.string.label_manage_stats_desc, allLabels.size, usedLabels.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Text(
                stringResource(R.string.label_manage_all_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allLabels.forEach { label ->
                    val isUsed = label in usedLabels
                    val resId = TagConfig.labelDisplayNameResId(label)
                    val displayName = if (resId != 0) context.getString(resId) else label
                    LabelChip(
                        label = displayName,
                        isUsed = isUsed,
                        onRename = {
                            renameText = label
                            showRenameDialog = label
                        },
                        onDelete = { showDeleteDialog = label }
                    )
                }
            }

            if (showDeleteDialog != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = null },
                    title = { Text(stringResource(R.string.label_manage_delete_title)) },
                    text = {
                        val delResId = TagConfig.labelDisplayNameResId(showDeleteDialog!!)
                        val delDisplayName = if (delResId != 0) context.getString(delResId) else showDeleteDialog!!
                        Text(stringResource(R.string.label_manage_delete_desc, delDisplayName))
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val label = showDeleteDialog!!
                            scope.launch {
                                todoViewModel.deleteByTag(label)
                                memoViewModel.deleteByTag(label)
                                showDeleteDialog = null
                                Toast.makeText(context, context.getString(R.string.label_manage_toast_deleted), Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text(stringResource(R.string.btn_delete), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = null }) {
                            Text(stringResource(R.string.btn_cancel))
                        }
                    }
                )
            }

            if (showRenameDialog != null) {
                AlertDialog(
                    onDismissRequest = { showRenameDialog = null },
                    title = { Text(stringResource(R.string.label_manage_rename_title)) },
                    text = {
                        Column {
                            Text(stringResource(R.string.label_manage_rename_desc))
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = renameText,
                                onValueChange = { renameText = it },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                ),
                                shape = MaterialTheme.shapes.small
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            val old = showRenameDialog!!
                            val newName = renameText.trim()
                            if (newName.isBlank() || newName == old) {
                                showRenameDialog = null
                                return@TextButton
                            }
                            val conflict = allLabels.contains(newName)
                            if (conflict) {
                                Toast.makeText(context, context.getString(R.string.label_manage_toast_conflict), Toast.LENGTH_SHORT).show()
                                return@TextButton
                            }
                            scope.launch {
                                todoViewModel.renameTag(old, newName)
                                memoViewModel.renameTag(old, newName)
                                showRenameDialog = null
                                Toast.makeText(context, context.getString(R.string.label_manage_toast_renamed), Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text(stringResource(R.string.btn_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showRenameDialog = null }) {
                            Text(stringResource(R.string.btn_cancel))
                        }
                    }
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.label_manage_add_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.label_manage_add_desc))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newLabel,
                        onValueChange = { newLabel = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.label_manage_add_placeholder)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        shape = MaterialTheme.shapes.small
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val name = newLabel.trim()
                    if (name.isBlank()) {
                        showAddDialog = false
                        return@TextButton
                    }
                    if (allLabels.contains(name)) {
                        Toast.makeText(context, context.getString(R.string.label_manage_toast_exists), Toast.LENGTH_SHORT).show()
                        return@TextButton
                    }
                    showAddDialog = false
                    newLabel = ""
                    Toast.makeText(context, context.getString(R.string.label_manage_toast_added), Toast.LENGTH_SHORT).show()
                }) {
                    Text(stringResource(R.string.btn_add))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false; newLabel = "" }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

@Composable
private fun LabelChip(
    label: String,
    isUsed: Boolean,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isUsed) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = if (isUsed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isUsed) FontWeight.Medium else FontWeight.Normal
            )

            Spacer(Modifier.width(8.dp))
            Icon(
                Octicons.Pencil16,
                stringResource(R.string.btn_edit),
                modifier = Modifier.size(14.dp).clickable(onClick = onRename),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(Modifier.width(6.dp))
            Icon(
                Octicons.Trash16,
                stringResource(R.string.btn_delete),
                modifier = Modifier.size(14.dp).clickable(onClick = onDelete),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )
        }
    }
}
