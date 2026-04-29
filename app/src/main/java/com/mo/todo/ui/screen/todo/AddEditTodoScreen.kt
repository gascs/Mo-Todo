package com.mo.todo.ui.screen.todo

import android.widget.Toast
import android.app.TimePickerDialog
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.data.model.TagConfig
import com.mo.todo.data.model.Todo
import com.mo.todo.ui.theme.PriorityHigh
import com.mo.todo.ui.theme.PriorityLow
import com.mo.todo.ui.theme.PriorityMedium
import com.mo.todo.ui.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

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
                    val fmt = SimpleDateFormat("M月d日 HH:mm", Locale.getDefault())
                    reminderText = fmt.format(Date(time))
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        calendar.timeInMillis = millis
                        showDatePicker = false
                        showTimePicker = true
                    }
                }) { Text("下一步") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("取消") }
            },
            shape = RoundedCornerShape(24.dp)
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        android.app.TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                reminderTime = calendar.timeInMillis
                val fmt = SimpleDateFormat("M月d日 HH:mm", Locale.getDefault())
                reminderText = fmt.format(Date(reminderTime))
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "编辑待办" else "新建待办", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))

            Text("标题", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("输入待办事项标题...") }, singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant, focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.small
            )

            Spacer(Modifier.height(20.dp))
            Text("详情备注", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                placeholder = { Text("添加详细描述（可选）") }, maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant, focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.small
            )

            Spacer(Modifier.height(20.dp))
            Text("提醒时间", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(Icons.Filled.CalendarMonth, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(reminderText, maxLines = 1)
                }
                if (reminderTime > 0) {
                    IconButton(onClick = { reminderTime = 0L; reminderText = "设置提醒" }, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Filled.Close, "清除提醒", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            val cal = Calendar.getInstance()
            val todayStart = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
            val tomorrowStart = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1); set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
            val nextWeek = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7); set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }

            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "今天" to todayStart.timeInMillis + 3600000L,
                    "明天 09:00" to tomorrowStart.timeInMillis,
                    "下周" to nextWeek.timeInMillis
                ).forEach { (label, millis) ->
                    Box(
                        Modifier.clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { reminderTime = millis; val fmt = SimpleDateFormat("M月d日 HH:mm", Locale.getDefault()); reminderText = fmt.format(Date(millis)) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) { Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("标签", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagConfig.todoTags.filter { it.key != "all" }.forEach { tag ->
                    val isSelected = selectedTag == tag.key
                    Box(
                        Modifier.clip(MaterialTheme.shapes.small)
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedTag = tag.key }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(tag.label, style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("优先级", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(Triple(2, "高", PriorityHigh), Triple(1, "中", PriorityMedium), Triple(0, "低", PriorityLow)).forEach { (p, label, color) ->
                    val isSelected = priority == p
                    Box(
                        Modifier.clip(MaterialTheme.shapes.small)
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { priority = p }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(if (isSelected) MaterialTheme.colorScheme.onPrimary else color))
                            Spacer(Modifier.width(6.dp))
                            Text(label, style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                        }
                    }
                }
            }

            Spacer(Modifier.height(36.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onNavigateBack, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.small) { Text("取消") }
                Button(onClick = {
                    if (title.isBlank()) { Toast.makeText(context, "请输入标题", Toast.LENGTH_SHORT).show(); return@Button }
                    val todo = Todo(id = todoId ?: 0, title = title, description = description.ifBlank { null }, tag = selectedTag, priority = priority, reminderTime = if (reminderTime > 0) reminderTime else null)
                    if (isEditing) viewModel.updateTodo(todo) else viewModel.insertTodo(todo)
                    onNavigateBack()
                }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), shape = MaterialTheme.shapes.small) { Text("保存") }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
