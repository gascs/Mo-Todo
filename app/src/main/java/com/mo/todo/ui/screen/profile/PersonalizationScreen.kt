package com.mo.todo.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
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
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("个性化", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Octicons.ArrowLeft24, "返回") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())) {
            Text("颜色主题", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                ColorTheme.entries.forEach { theme ->
                    val (light, _) = getThemeColors(theme)
                    val selected = colorThemeKey == theme.key
                    Box(Modifier.size(44.dp).clip(CircleShape).background(light).clickable { scope.launch { viewModel.setColorTheme(theme.key) } },
                        contentAlignment = Alignment.Center) {
                        if (selected) Box(Modifier.size(14.dp).clip(CircleShape).background(androidx.compose.ui.graphics.Color.White))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("字体大小", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                FontSize.entries.forEach { fs ->
                    val selected = fontSizeKey == fs.key
                    Card(
                        Modifier.weight(1f).clickable { scope.launch { viewModel.setFontSize(fs.key) } },
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp)
                    ) {
                        Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                            Text(fs.label, style = MaterialTheme.typography.bodyLarge, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("圆角风格", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                CornerStyle.entries.forEach { cs ->
                    val selected = cornerStyleKey == cs.key
                    Card(
                        Modifier.weight(1f).clickable { scope.launch { viewModel.setCornerStyle(cs.key) } },
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp)
                    ) {
                        Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                            Text(cs.label, style = MaterialTheme.typography.bodyLarge, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("深浅模式", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                listOf(ThemeMode.SYSTEM to "跟随系统", ThemeMode.LIGHT to "浅色", ThemeMode.DARK to "深色").forEach { (mode, label) ->
                    val selected = themeMode == mode
                    Card(
                        Modifier.weight(1f).clickable { scope.launch { viewModel.setThemeMode(mode) } },
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp)
                    ) {
                        Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                            Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("动态取色", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                listOf(false to "自定义配色", true to "取色自壁纸").forEach { (enabled, label) ->
                    val selected = isDynamic == enabled
                    Card(Modifier.weight(1f).clickable { scope.launch { viewModel.setDynamicColor(enabled) } },
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp)
                    ) {
                        Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                            Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("列表密度", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                ListDensity.entries.forEach { density ->
                    val selected = listDensity == density
                    Card(
                        Modifier.weight(1f).clickable { scope.launch { viewModel.setListDensity(density.key) } },
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp)
                    ) {
                        Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                            Text(density.label, style = MaterialTheme.typography.bodyLarge, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("新建待办默认优先级", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                listOf(Triple(2, "高", PriorityHigh), Triple(1, "中", PriorityMedium), Triple(0, "低", PriorityLow)).forEach { (p, label, color) ->
                    val selected = defaultPriority == p
                    Card(
                        Modifier.weight(1f).clickable { scope.launch { viewModel.setDefaultPriority(p) } },
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 2.dp else 0.dp)
                    ) {
                        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.size(12.dp).clip(CircleShape).background(color))
                            Spacer(Modifier.height(6.dp))
                            Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("通知振动", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(if (notificationVibrate) "提醒时将振动" else "提醒时仅通知音", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                Switch(checked = notificationVibrate, onCheckedChange = { scope.launch { viewModel.setNotificationVibrate(it) } },
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = MaterialTheme.colorScheme.primaryContainer))
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
