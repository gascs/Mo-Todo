package com.motut.mo.ui.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.motut.mo.data.Priority
import com.motut.mo.data.Todo
import com.motut.mo.util.DateFormats
import java.time.LocalDate
import java.time.LocalTime

/**
 * 新建待办 BottomSheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, LocalDate?, LocalTime?, Priority) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val initialDateMillis = remember(selectedDate) {
        selectedDate?.atStartOfDay(java.time.ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)
    val initialHour = remember(selectedTime) { selectedTime?.hour ?: 0 }
    val initialMinute = remember(selectedTime) { selectedTime?.minute ?: 0 }
    val timePickerState = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute)

    if (showDatePicker) {
        Dialog(onDismissRequest = { showDatePicker = false }) {
            Card(modifier = Modifier.padding(16.dp), shape = MaterialTheme.shapes.large) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DatePicker(state = datePickerState)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDatePicker = false }) { Text("取消") }
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let {
                                selectedDate = java.time.Instant.ofEpochMilli(it)
                                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                            }
                            showDatePicker = false
                        }) { Text("确定") }
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Card(modifier = Modifier.padding(16.dp), shape = MaterialTheme.shapes.large) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("选择时间", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
                    TimePicker(state = timePickerState)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showTimePicker = false }) { Text("取消") }
                        TextButton(onClick = {
                            selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                            showTimePicker = false
                        }) { Text("确定") }
                    }
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("新建待办", color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize().padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                    shape = MaterialTheme.shapes.large) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.RadioButtonUnchecked, null,
                                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                            OutlinedTextField(value = title, onValueChange = { title = it },
                                label = { Text("任务标题") }, modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent))
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(value = content, onValueChange = { content = it },
                    label = { Text("详细内容") }, minLines = 3, maxLines = 6,
                    modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)

                OutlinedTextField(value = location, onValueChange = { location = it },
                    label = { Text("地点") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium) {
                        Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp))
                        Text(if (selectedDate != null) selectedDate!!.format(DateFormats.monthDay) else "选择日期")
                    }
                    OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium) {
                        Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp))
                        Text(if (selectedTime != null) selectedTime!!.format(DateFormats.time24h) else "选择时间")
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("重要程度", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        FilterChip(selected = selectedPriority == Priority.LOW, onClick = { selectedPriority = Priority.LOW },
                            label = { Text("低") }, modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer))
                        FilterChip(selected = selectedPriority == Priority.MEDIUM, onClick = { selectedPriority = Priority.MEDIUM },
                            label = { Text("中") }, modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer))
                        FilterChip(selected = selectedPriority == Priority.HIGH, onClick = { selectedPriority = Priority.HIGH },
                            label = { Text("高") }, modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer))
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        IconButton(onClick = {}) { Icon(Icons.Default.Palette, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                        IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismiss) { Text("取消", color = MaterialTheme.colorScheme.primary) }
                        Button(onClick = { if (title.isNotBlank()) onConfirm(title, content, location, selectedDate, selectedTime, selectedPriority) },
                            enabled = title.isNotBlank(), shape = MaterialTheme.shapes.medium) { Text("完成") }
                    }
                }
            }
        }
    }
}
