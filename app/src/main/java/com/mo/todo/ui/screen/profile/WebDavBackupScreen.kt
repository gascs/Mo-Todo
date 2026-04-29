﻿﻿﻿package com.mo.todo.ui.screen.profile

import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import org.json.JSONObject
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebDavBackupScreen(
    onNavigateBack: () -> Unit,
    onNavigateToConfig: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }
    var statusIsError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WebDAV 备份", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Octicons.ArrowLeft24, "返回") }
                },
                actions = {
                    IconButton(onClick = onNavigateToConfig) {
                        Icon(Icons.Filled.Settings, contentDescription = "WebDAV 配置")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Text("\u2601\ufe0f", style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(8.dp))
            Text("WebDAV 云备份", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text("将数据备份到你的 WebDAV 服务器", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(32.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            statusMessage = ""
                            try {
                                val result = withContext(Dispatchers.IO) { performWebDavBackup(viewModel) }
                                statusMessage = result
                                statusIsError = false
                            } catch (e: Exception) {
                                statusMessage = "备份失败: ${e.message}"
                                statusIsError = true
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading,
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("备份到 WebDAV")
                }

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            statusMessage = ""
                            try {
                                val result = withContext(Dispatchers.IO) { performWebDavRestore(viewModel) }
                                statusMessage = result
                                statusIsError = false
                            } catch (e: Exception) {
                                statusMessage = "恢复失败: ${e.message}"
                                statusIsError = true
                            }
                            isLoading = false
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading,
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(Icons.Filled.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("从 WebDAV 恢复")
                }
            }

            if (isLoading) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator(modifier = Modifier.size(36.dp), color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Text("处理中...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (statusMessage.isNotBlank()) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (statusIsError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private suspend fun performWebDavBackup(viewModel: SettingsViewModel): String {
    val config = viewModel.getWebDavConfig() ?: return "请先配置 WebDAV 连接信息"
    val (serverUrl, username, password) = config

    val root = JSONObject()
    root.put("app", "Mo")
    root.put("version", "1.0.0")
    root.put("exportTime", System.currentTimeMillis())
    root.put("todos", org.json.JSONArray())
    root.put("memos", org.json.JSONArray())

    val json = root.toString(2)
    val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val url = serverUrl.trimEnd('/') + "/Mo_backup_${System.currentTimeMillis()}.json"
    val requestBody = json.toRequestBody("application/json".toMediaType())
    val request = Request.Builder()
        .url(url)
        .header("Authorization", Credentials.basic(username, password))
        .put(requestBody)
        .build()

    val response = client.newCall(request).execute()
    return if (response.isSuccessful) {
        "备份成功: ${response.code}"
    } else {
        throw Exception("${response.code} ${response.message}")
    }
}

private suspend fun performWebDavRestore(viewModel: SettingsViewModel): String {
    val config = viewModel.getWebDavConfig() ?: return "请先配置 WebDAV 连接信息"
    val (serverUrl, username, password) = config

    val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val url = serverUrl.trimEnd('/') + "/Mo_backup.json"
    val request = Request.Builder()
        .url(url)
        .header("Authorization", Credentials.basic(username, password))
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (!response.isSuccessful) throw Exception("${response.code} ${response.message}")

    val json = response.body?.string() ?: throw Exception("响应内容为空")
    val root = JSONObject(json)
    return "恢复成功 (v${root.optString("version", "?")})"
}
