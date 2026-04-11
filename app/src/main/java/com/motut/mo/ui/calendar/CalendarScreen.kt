package com.motut.mo.ui.calendar

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.motut.mo.data.Todo
import com.motut.mo.data.Priority
import com.motut.mo.ui.theme.AppColors
import com.motut.mo.util.DateFormats
import java.time.LocalDate
import kotlinx.coroutines.launch

/**
 * 日历视图 - 展示月历网格和选中日期的任务列表
 * 修复了原代码中的 MainScope() 内存泄漏问题（P1-7）
 */
@Composable
fun CalendarScreen(
    todos: List<Todo>,
    onTodoClick: (Todo) -> Unit
) {
    val today = remember { LocalDate.now() }
    var selectedMonth by remember { mutableStateOf(today) }
    var selectedDate by remember { mutableStateOf(today) }
    val daysOfWeek = listOf("日", "一", "二", "三", "四", "五", "六")

    // 使用 Composable 的 coroutineScope 代替 MainScope()，避免内存泄漏
    val calendarScope = rememberCoroutineScope()

    var isMonthChanging by remember { mutableStateOf(false) }

    val firstDayOfMonth = selectedMonth.withDayOfMonth(1)
    val daysInMonth = selectedMonth.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

    val selectedDateTodos = remember(todos, selectedDate) {
        todos.filter { it.date == selectedDate && !it.isCompleted }
    }

    // 生成日历网格数据
    val calendarDays = remember(firstDayOfMonth, daysInMonth, firstDayOfWeek) {
        val days = mutableListOf<LocalDate?>()
        repeat(firstDayOfWeek) { days.add(null) }
        for (day in 1..daysInMonth) { days.add(selectedMonth.withDayOfMonth(day)) }
        while (days.size % 7 != 0) { days.add(null) }
        days
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 月份导航
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { isMonthChanging = true; selectedMonth = selectedMonth.minusMonths(1) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "上个月",
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = selectedMonth.format(DateFormats.monthYearChinese),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Text(text = "1 - $daysInMonth",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { isMonthChanging = true; selectedMonth = selectedMonth.plusMonths(1) }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "下个月",
                                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                        }
                        if (selectedMonth != today.withDayOfMonth(1)) {
                            FilledTonalButton(
                                onClick = { isMonthChanging = true; selectedMonth = today.withDayOfMonth(1); selectedDate = today },
                                shape = RoundedCornerShape(12.dp), modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Today, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("今天", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 星期标签
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    daysOfWeek.forEachIndexed { index, day ->
                        val isWeekend = index == 0 || index == 6
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isWeekend) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else Color.Transparent
                        ) {
                            Text(text = day, modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isWeekend) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // 日历网格
                calendarDays.chunked(7).forEachIndexed { _, week ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        week.forEach { date ->
                            val isValidDate = date != null
                            val isToday = date == today
                            val isSelected = date == selectedDate
                            val hasEvent = date?.let { d -> todos.any { it.date == d && !it.isCompleted } } ?: false
                            val hasHighPriorityEvent = date?.let { d ->
                                todos.any { it.date == d && !it.isCompleted && it.priority == Priority.HIGH }
                            } ?: false
                            val isWeekend = date?.dayOfWeek?.value?.let { it == 7 || it == 1 } ?: false

                            var isPressed by remember { mutableStateOf(false) }
                            val scale by animateFloatAsState(
                                targetValue = if (isPressed) 0.9f else 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
                                label = "dayScale"
                            )

                            Box(
                                modifier = Modifier
                                    .weight(1f).aspectRatio(1f).padding(2.dp)
                                    .graphicsLayer { scaleX = scale; scaleY = scale }
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> AppColors.PrimaryModern
                                            isToday -> MaterialTheme.colorScheme.primaryContainer
                                            else -> Color.Transparent
                                        }
                                    )
                                    .then(if (isValidDate) Modifier.clickable {
                                        isPressed = true
                                        selectedDate = date!!
                                        // 使用 calendarScope 替代 MainScope()，避免泄漏
                                        calendarScope.launch { kotlinx.coroutines.delay(100); isPressed = false }
                                    } else Modifier),
                                contentAlignment = Alignment.Center
                            ) {
                                if (date != null) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = when {
                                                isSelected -> Color.White
                                                isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                                                isWeekend -> MaterialTheme.colorScheme.error
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                        if (hasEvent) {
                                            Spacer(Modifier.height(2.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                if (hasHighPriorityEvent) {
                                                    Box(modifier = Modifier.size(6.dp).background(
                                                        color = if (isSelected) Color.White else AppColors.PriorityHighModern,
                                                        shape = CircleShape
                                                    ))
                                                } else {
                                                    Box(modifier = Modifier.size(6.dp).background(
                                                        color = if (isSelected) Color.White else AppColors.PrimaryModern,
                                                        shape = CircleShape
                                                    ))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 选中日期的任务列表
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(modifier = Modifier.size(8.dp), color = AppColors.PrimaryModern, shape = CircleShape) {}
                    Text(text = selectedDate.format(DateFormats.fullChinese),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                }
                if (selectedDateTodos.isNotEmpty()) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape) {
                        Text(text = "${selectedDateTodos.size}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            if (selectedDateTodos.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(modifier = Modifier.size(64.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = CircleShape) {
                            Icon(Icons.Default.EventAvailable, null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.padding(16.dp).size(32.dp))
                        }
                        Text(text = "这一天没有安排",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }
            } else {
                selectedDateTodos.forEach { todo ->
                    CalendarEventCard(todo = todo, onClick = { onTodoClick(todo) })
                }
            }
        }
    }
}
