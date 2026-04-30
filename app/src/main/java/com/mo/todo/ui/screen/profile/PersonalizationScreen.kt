package com.mo.todo.ui.screen.profile

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.R
import com.mo.todo.ui.theme.ColorTheme
import com.mo.todo.ui.theme.CornerStyle
import com.mo.todo.ui.theme.FontSize
import com.mo.todo.ui.theme.PriorityHigh
import com.mo.todo.ui.theme.PriorityLow
import com.mo.todo.ui.theme.PriorityMedium
import com.mo.todo.ui.theme.getThemeColors
import com.mo.todo.ui.viewmodel.ListDensity
import com.mo.todo.ui.viewmodel.SettingsViewModel
import com.mo.todo.ui.viewmodel.ThemeMode
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PersonalizationScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val colorThemeKey by viewModel.colorTheme.collectAsState()
    val fontSizeKey by viewModel.fontSize.collectAsState()
    val cornerStyleKey by viewModel.cornerStyle.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val isDynamic by viewModel.isDynamicColor.collectAsState()
    val listDensity by viewModel.listDensity.collectAsState(initial = ListDensity.NORMAL)
    val notificationVibrate by viewModel.notificationVibrate.collectAsState()
    val defaultPriority by viewModel.defaultPriority.collectAsState()
    val language by viewModel.language.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.personalization_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Octicons.ArrowLeft24, stringResource(R.string.btn_cancel)) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())
        ) {
            // Color Theme
            SectionTitle(stringResource(R.string.personalization_color_theme))
            ColorTheme.entries.forEach { theme ->
                ThemeOptionRow(
                    name = stringResource(theme.nameResId()),
                    color = getThemeColors(theme).first,
                    isSelected = colorThemeKey == theme.key,
                    onClick = { scope.launch { viewModel.setColorTheme(theme.key) } }
                )
            }

            Spacer(Modifier.height(16.dp))

            // Font Size
            SectionTitle(stringResource(R.string.personalization_font_size))
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FontSize.entries.forEach { option ->
                    SelectableChip(
                        text = stringResource(option.nameResId()),
                        selected = fontSizeKey == option.key,
                        onClick = { scope.launch { viewModel.setFontSize(option.key) } }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Corner Style
            SectionTitle(stringResource(R.string.personalization_corner_style))
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CornerStyle.entries.forEach { style ->
                    SelectableChip(
                        text = stringResource(style.nameResId()),
                        selected = cornerStyleKey == style.key,
                        onClick = { scope.launch { viewModel.setCornerStyle(style.key) } }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Theme Mode
            SectionTitle(stringResource(R.string.personalization_theme_mode))
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeMode.entries.forEach { mode ->
                    SelectableChip(
                        text = stringResource(mode.nameResId()),
                        selected = themeMode == mode,
                        onClick = { scope.launch { viewModel.setThemeMode(mode) } }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Dynamic Color
            SettingSwitchCard(
                title = stringResource(R.string.personalization_dynamic_color),
                subtitle = stringResource(R.string.personalization_dynamic_color_desc),
                checked = isDynamic,
                onCheckedChange = { scope.launch { viewModel.setDynamicColor(it) } }
            )

            Spacer(Modifier.height(16.dp))

            // List Density
            SectionTitle(stringResource(R.string.personalization_list_density))
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ListDensity.entries.forEach { density ->
                    SelectableChip(
                        text = stringResource(density.nameResId()),
                        selected = listDensity == density,
                        onClick = { scope.launch { viewModel.setListDensity(density.key) } }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Default Priority
            SectionTitle(stringResource(R.string.personalization_default_priority))
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple(2, stringResource(R.string.priority_high), PriorityHigh),
                    Triple(1, stringResource(R.string.priority_medium), PriorityMedium),
                    Triple(0, stringResource(R.string.priority_low), PriorityLow)
                ).forEach { (p, label, color) ->
                    Box(
                        Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(if (defaultPriority == p) color else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { scope.launch { viewModel.setDefaultPriority(p) } }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(label, style = MaterialTheme.typography.labelLarge,
                            color = if (defaultPriority == p) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (defaultPriority == p) FontWeight.SemiBold else FontWeight.Normal)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Notification Vibration
            SettingSwitchCard(
                title = stringResource(R.string.personalization_notify_vibrate),
                subtitle = stringResource(R.string.personalization_notify_vibrate_desc),
                checked = notificationVibrate,
                onCheckedChange = { scope.launch { viewModel.setNotificationVibrate(it) } }
            )

            Spacer(Modifier.height(16.dp))

            // Language
            SectionTitle(stringResource(R.string.personalization_language))
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectableChip(text = stringResource(R.string.personalization_language_system), selected = language == "system", onClick = { scope.launch { viewModel.setLanguage("system") } })
                SelectableChip(text = stringResource(R.string.personalization_language_zh), selected = language == "zh", onClick = { scope.launch { viewModel.setLanguage("zh") } })
                SelectableChip(text = stringResource(R.string.personalization_language_en), selected = language == "en", onClick = { scope.launch { viewModel.setLanguage("en") } })
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun SelectableChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun ThemeOptionRow(name: String, color: androidx.compose.ui.graphics.Color, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 3.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape).background(color),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) Icon(Octicons.Check16, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(name, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
private fun SettingSwitchCard(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

private fun ColorTheme.nameResId(): Int = when (this) {
    ColorTheme.FOREST -> R.string.personalization_theme_forest
    ColorTheme.OCEAN -> R.string.personalization_theme_ocean
    ColorTheme.SUNSET -> R.string.personalization_theme_sunset
    ColorTheme.LAVENDER -> R.string.personalization_theme_lavender
    ColorTheme.ROSEGOLD -> R.string.personalization_theme_rosegold
}

private fun FontSize.nameResId(): Int = when (this) {
    FontSize.SMALL -> R.string.personalization_font_small
    FontSize.MEDIUM -> R.string.personalization_font_normal
    FontSize.LARGE -> R.string.personalization_font_large
}

private fun CornerStyle.nameResId(): Int = when (this) {
    CornerStyle.ROUNDED -> R.string.personalization_corner_round
    CornerStyle.SQUARE -> R.string.personalization_corner_square
    CornerStyle.EXTRA_ROUNDED -> R.string.personalization_corner_extra_round
}

private fun ThemeMode.nameResId(): Int = when (this) {
    ThemeMode.SYSTEM -> R.string.personalization_theme_follow
    ThemeMode.LIGHT -> R.string.personalization_theme_light
    ThemeMode.DARK -> R.string.personalization_theme_dark
}

private fun ListDensity.nameResId(): Int = when (this) {
    ListDensity.COMPACT -> R.string.personalization_density_compact
    ListDensity.NORMAL -> R.string.personalization_density_normal
    ListDensity.RELAXED -> R.string.personalization_density_relaxed
}
