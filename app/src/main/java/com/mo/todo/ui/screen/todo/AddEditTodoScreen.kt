package com.mo.todo.ui.screen.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.data.model.Todo
import com.mo.todo.ui.viewmodel.TodoViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTodoScreen(
    todoId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: TodoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("work") }
    var priority by remember { mutableStateOf(1) }
    var reminderTime by remember { mutableLongStateOf(0L) }
    var reminderText by remember { mutableStateOf("设置提醒") }

    val isEditing = todoId != null
    val calendar = remember { Calendar.getInstance() }

    LaunchedEffect(todoId) {
        if (todoId != null && todoId > 0) {
            val existing = viewModel.getTodoById(todoId)
            if (existing != null) {
                title = existing.title
                description = existing.description ?: ""
                selectedTag = existing.tag
                priority = existing.priority
                existing.reminderTime?.let { time ->
                    reminderTime = time
                    calendar.timeInMillis = time
                    reminderText = "${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.DAY_OF_MONTH)} ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE).toString().padStart(2, '0')}"
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "编辑待办" else "新建待办",
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
                placeholder = { Text("输入待办事项标题...") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "详情备注",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = { Text("添加详细描述（可选）") },
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "提醒时间",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(Calendar.YEAR, year)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                    calendar.set(Calendar.MINUTE, minute)
                                    reminderTime = calendar.timeInMillis
                                    reminderText = "${month + 1}/${dayOfMonth} ${hourOfDay}:${minute.toString().padStart(2, '0')}"
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = reminderText)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "标签",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                todoTags.filter { it.key != "all" }.forEach { tag ->
                    FilterChip(
                        selected = selectedTag == tag.key,
                        onClick = { selectedTag = tag.key },
                        label = { Text(tag.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "优先级",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple(2, "高", com.mo.todo.ui.theme.PriorityHigh),
                    Triple(1, "中", com.mo.todo.ui.theme.PriorityMedium),
                    Triple(0, "低", com.mo.todo.ui.theme.PriorityLow)
                ).forEach { (p, label, color) ->
                            FilterChip(
                                selected = priority == p,
                                onClick = { priority = p },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(label)
                                    }
                                },
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
                        val todo = Todo(
                            id = todoId ?: 0,
                            title = title,
                            description = description.ifBlank { null },
                            tag = selectedTag,
                            priority = priority,
                            reminderTime = if (reminderTime > 0) reminderTime else null
                        )
                        if (isEditing) {
                            viewModel.updateTodo(todo)
                        } else {
                            viewModel.insertTodo(todo)
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
