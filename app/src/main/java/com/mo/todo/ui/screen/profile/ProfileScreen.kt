package com.mo.todo.ui.screen.profile

import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import compose.icons.Octicons
import compose.icons.octicons.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.R
import com.mo.todo.ui.viewmodel.SettingsViewModel
import com.mo.todo.ui.viewmodel.StatsViewModel
import java.util.Calendar

@Composable
fun ProfileScreen(
    onNavigateToLabelManagement: () -> Unit,
    onNavigateToReminderSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToPersonalization: () -> Unit,
    onNavigateToDataManagement: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    statsViewModel: StatsViewModel = hiltViewModel()
) {
    val nickname by settingsViewModel.nickname.collectAsState(initial = "")
    val context = LocalContext.current

    val todoTotal by statsViewModel.todoTotal.collectAsState()
    val todoCompleted by statsViewModel.todoCompleted.collectAsState()
    val todoActive by statsViewModel.todoActive.collectAsState()
    val memoTotal by statsViewModel.memoTotal.collectAsState()
    val memoStarred by statsViewModel.memoStarred.collectAsState()
    val completionRate by statsViewModel.completionRate.collectAsState()

    var greeting by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        greeting = when {
            hour < 12 -> context.getString(R.string.profile_morning)
            hour < 18 -> context.getString(R.string.profile_afternoon)
            else -> context.getString(R.string.profile_evening)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(top = 60.dp, bottom = 100.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = "$greeting，",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = nickname.ifBlank { stringResource(R.string.profile_default_nickname) },
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.profile_today_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        DataOverviewCard(
            completionRate = completionRate,
            todoTotal = todoTotal,
            todoCompleted = todoCompleted,
            todoActive = todoActive,
            memoTotal = memoTotal,
            memoStarred = memoStarred
        )

        Spacer(modifier = Modifier.height(28.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            SectionHeader(title = stringResource(R.string.profile_section_settings))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    SettingItem(
                        icon = Octicons.Paintbrush16,
                        title = stringResource(R.string.profile_personalization),
                        subtitle = stringResource(R.string.profile_personalization_desc),
                        onClick = onNavigateToPersonalization
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    SettingItem(
                        icon = Octicons.Bell16,
                        title = stringResource(R.string.profile_reminder_settings),
                        subtitle = stringResource(R.string.profile_reminder_desc),
                        onClick = onNavigateToReminderSettings
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    SettingItem(
                        icon = Octicons.Tag16,
                        title = stringResource(R.string.profile_label_manage),
                        subtitle = stringResource(R.string.profile_label_manage_desc),
                        onClick = onNavigateToLabelManagement
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            SectionHeader(title = stringResource(R.string.profile_section_data))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                SettingItem(
                    icon = Octicons.Database16,
                    title = stringResource(R.string.profile_data_manage),
                    subtitle = stringResource(R.string.profile_data_manage_desc),
                    onClick = onNavigateToDataManagement
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            SectionHeader(title = stringResource(R.string.profile_section_about))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                SettingItem(
                    icon = Octicons.Info16,
                    title = stringResource(R.string.profile_about),
                    subtitle = stringResource(R.string.profile_about_desc),
                    onClick = onNavigateToAbout
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.profile_share_text))
                    }
                    context.startActivity(Intent.createChooser(shareIntent, null))
                },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Octicons.Share16, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.profile_share),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun DataOverviewCard(
    completionRate: Int,
    todoTotal: Int,
    todoCompleted: Int,
    todoActive: Int,
    memoTotal: Int,
    memoStarred: Int
) {
    val animatedProgress by animateFloatAsState(
        targetValue = completionRate / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )

    Card(
        modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.profile_data_overview),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.profile_completion_rate) + " $completionRate%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem(label = stringResource(R.string.todo_title), value = "$todoTotal", sub = stringResource(R.string.profile_completed) + " $todoCompleted")
                StatItem(label = stringResource(R.string.profile_active_todos), value = "$todoActive", sub = stringResource(R.string.profile_completion_rate) + " $completionRate%")
                StatItem(label = stringResource(R.string.memo_title), value = "$memoTotal", sub = stringResource(R.string.profile_starred) + " $memoStarred")
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, sub: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontSize = 9.sp)
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Icon(Octicons.ChevronRight16, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
    }
}
