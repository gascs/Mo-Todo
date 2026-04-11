package com.motut.mo.ui.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 新建备忘录 BottomSheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(text = "新建备忘录", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            OutlinedTextField(value = title, onValueChange = { title = it },
                label = { Text("标题") }, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large)

            OutlinedTextField(value = content, onValueChange = { content = it },
                label = { Text("内容") }, minLines = 5, modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = {}) { Icon(Icons.Default.Image, null) }
                IconButton(onClick = {}) { Icon(Icons.Default.Mic, null) }
                IconButton(onClick = {}) { Icon(Icons.Default.AttachFile, null) }
                Spacer(Modifier.weight(1f))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onDismiss) { Text("取消") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { if (title.isNotBlank()) onConfirm(title, content) },
                    enabled = title.isNotBlank(), shape = MaterialTheme.shapes.large) { Text("保存备忘录") }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
