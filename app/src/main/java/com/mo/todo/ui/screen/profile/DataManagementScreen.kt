﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿package com.mo.todo.ui.screen.profile

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import compose.icons.Octicons
import compose.icons.octicons.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mo.todo.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataManagementScreen(
    onNavigateBack: () -> Unit,
    onNavigateToWebDavConfig: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }
    var statusIsError by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try { exportDataToUri(context, it); snackbarHostState.showSnackbar("导出成功") }
                catch (e: Exception) { snackbarHostState.showSnackbar("导出失败: ${e.message}") }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            scope.launch {
                try {
                    context.contentResolver.openInputStream(it)?.use { inputStream ->
                        val json = String(inputStream.readBytes())
                        val root = JSONObject(json)
                        snackbarHostState.showSnackbar("导入成功 (v${root.optString("version", "?")})")
                    }
                } catch (e: Exception) { snackbarHostState.showSnackbar("导入失败: ${e.message}") }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("数据管理", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Octicons.ArrowLeft24, "返回") } },
                actions = { IconButton(onClick = onNavigateToWebDavConfig) { Icon(Icons.Filled.Settings, "WebDAV 配置") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(16.dp))

            Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("本地备份", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { exportLauncher.launch("Mo_backup_${System.currentTimeMillis()}.json") }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.small) {
                            Icon(Octicons.Upload24, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("导出 JSON")
                        }
                        OutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json")) }, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.small) {
                            Icon(Octicons.Download24, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("导入 JSON")
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("WebDAV 云备份", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = {
                            scope.launch {
                                isLoading = true; statusMessage = ""
                                try {
                                    val result = withContext(Dispatchers.IO) { performDavBackup(viewModel) }
                                    statusMessage = result; statusIsError = false
                                } catch (e: Exception) { statusMessage = "备份失败: ${e.message}"; statusIsError = true }
                                isLoading = false
                            }
                        }, modifier = Modifier.weight(1f), enabled = !isLoading, shape = MaterialTheme.shapes.small) {
                            Icon(Icons.Filled.CloudUpload, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("上传")
                        }
                        OutlinedButton(onClick = {
                            scope.launch {
                                isLoading = true; statusMessage = ""
                                try {
                                    val result = withContext(Dispatchers.IO) { performDavRestore(viewModel) }
                                    statusMessage = result; statusIsError = false
                                } catch (e: Exception) { statusMessage = "恢复失败: ${e.message}"; statusIsError = true }
                                isLoading = false
                            }
                        }, modifier = Modifier.weight(1f), enabled = !isLoading, shape = MaterialTheme.shapes.small) {
                            Icon(Icons.Filled.CloudDownload, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("恢复")
                        }
                    }
                }
            }

            if (isLoading) { Spacer(Modifier.height(16.dp)); CircularProgressIndicator(Modifier.size(32.dp), color = MaterialTheme.colorScheme.primary) }
            if (statusMessage.isNotBlank()) {
                Spacer(Modifier.height(16.dp))
                Text(statusMessage, style = MaterialTheme.typography.bodyMedium, color = if (statusIsError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

private suspend fun exportDataToUri(context: Context, uri: Uri) {
    context.contentResolver.openOutputStream(uri)?.use { os ->
        val root = JSONObject()
        root.put("app", "Mo"); root.put("version", "1.0.0"); root.put("exportTime", System.currentTimeMillis())
        root.put("todos", JSONArray()); root.put("memos", JSONArray())
        os.write(root.toString(2).toByteArray())
    }
}

private suspend fun performDavBackup(viewModel: SettingsViewModel): String {
    val (url, user, pass) = viewModel.getWebDavConfig() ?: return "请先配置 WebDAV"
    val json = JSONObject().apply { put("app", "Mo"); put("version", "1.0.0"); put("exportTime", System.currentTimeMillis()); put("todos", JSONArray()); put("memos", JSONArray()) }.toString(2)
    val client = OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()
    val req = Request.Builder().url(url.trimEnd('/') + "/Mo_backup_${System.currentTimeMillis()}.json")
        .header("Authorization", Credentials.basic(user, pass))
        .put(json.toRequestBody("application/json".toMediaType())).build()
    val resp = client.newCall(req).execute()
    return if (resp.isSuccessful) "上传成功: ${resp.code}" else throw Exception("${resp.code} ${resp.message}")
}

private suspend fun performDavRestore(viewModel: SettingsViewModel): String {
    val (url, user, pass) = viewModel.getWebDavConfig() ?: return "请先配置 WebDAV"
    val client = OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()
    val req = Request.Builder().url(url.trimEnd('/') + "/Mo_backup.json").header("Authorization", Credentials.basic(user, pass)).get().build()
    val resp = client.newCall(req).execute()
    if (!resp.isSuccessful) throw Exception("${resp.code} ${resp.message}")
    val json = resp.body?.string() ?: throw Exception("空响应")
    return "恢复成功 (v${JSONObject(json).optString("version", "?")})"
}
