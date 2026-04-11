package com.motut.mo.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight

/**
 * 隐私政策页面 - 使用 BaseSettingsDialog 统一布局
 */
@Composable
fun PrivacyPolicyScreen(onDismiss: () -> Unit) {
    BaseSettingsDialog(title = "隐私政策", onDismiss = onDismiss) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "隐私政策", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            
            Text("最后更新：2024年1月1日\n\n我们非常重视您的隐私。本隐私政策说明了我们如何收集、使用和保护您在使用 MoTodo 应用时的个人信息。\n",
                style = MaterialTheme.typography.bodyMedium)
            
            SectionTitle("1. 我们收集的信息")
            Text("• 应用使用数据（匿名统计）\n• 设备信息（型号、系统版本）\n• 崩溃日志（用于修复问题）\n• 用户偏好设置（本地存储，不上传）\n", style = MaterialTheme.typography.bodyMedium)

            SectionTitle("2. 信息的使用")
            Text("• 改进应用功能和性能\n• 修复bug和提升稳定性\n• 分析用户使用趋势以优化体验\n• 绝不向第三方出售或共享您的个人信息\n", style = MaterialTheme.typography.bodyMedium)

            SectionTitle("3. 数据存储")
            Text("• 所有个人数据（待办、备忘录）仅存储在您的设备上\n• 备份文件由您自主管理\n• 云同步功能即将上线（将采用端到端加密）\n", style = MaterialTheme.typography.bodyMedium)

            SectionTitle("4. 您的权利")
            Text("• 可随时导出您的所有数据\n• 可随时删除应用及所有相关数据\n• 可选择是否发送匿名使用统计数据\n", style = MaterialTheme.typography.bodyMedium)

            SectionTitle("5. 联系我们")
            Text("如有任何疑问，请通过「帮助与反馈」联系我们：support@motut.app\n",
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/**
 * 用户协议页面
 */
@Composable
fun UserAgreementScreen(onDismiss: () -> Unit) {
    BaseSettingsDialog(title = "用户协议", onDismiss = onDismiss) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "用户服务协议", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("欢迎您使用 MoTodo（以下简称\"本应用\"）。本协议是您与 MoTuT 团队之间关于使用本应用的法律协议。\n",
                style = MaterialTheme.typography.bodyMedium)

            SectionTitle("1. 服务说明")
            Text("本应用是一个个人效率工具，提供待办事项、备忘录、日历等功能，帮助您高效管理工作和生活事务。\n", style = MaterialTheme.typography.bodyMedium)

            SectionTitle("2. 用户义务")
            Text("• 合法合规使用本应用\n• 不利用本应用从事违法违规活动\n• 自行负责数据的备份和安全\n", style = MaterialTheme.typography.bodyMedium)

            SectionTitle("3. 知识产权")
            Text("本应用的代码、UI设计、图标等均受知识产权法律保护。未经授权不得复制、修改、分发。\n", style = MaterialTheme.typography.bodyMedium)

            SectionTitle("4. 免责声明")
            Text("• 本应用按\"现状\"提供，不作任何明示或暗示保证\n• 因不可抗力导致的服务中断不承担责任\n• 用户自行承担使用风险\n", style = MaterialTheme.typography.bodyMedium)

            SectionTitle("5. 协议变更")
            Text("我们会不时更新本协议。重大变更将通过应用内公告通知您。继续使用即视为接受新协议。\n",
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/**
 * 开源声明页面
 */
@Composable
fun OpenSourceStatementScreen(onDismiss: () -> Unit) {
    BaseSettingsDialog(title = "开源声明", onDismiss = onDismiss) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "开源声明", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("MoTodo 是一个开源项目，感谢以下开源社区的支持：\n",
                style = MaterialTheme.typography.bodyMedium)

            LibraryItem("Jetpack Compose", "Google", "现代 Android UI 工具包")
            LibraryItem("Material Design 3", "Google", "Material 设计系统")
            LibraryItem("Kotlin Coroutines", "JetBrains", "Kotlin 协程库")
            LibraryItem("AndroidX", "AOSP Community", "Android 扩展库")
            LibraryItem("SQLite", "SQLite Consortium", "轻量级数据库")

            Text("\n完整开源许可证信息可在「关于 > 开源许可证」中查看。",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * 开源许可证页面
 */
@Composable
fun LicenseScreen(onDismiss: () -> Unit) {
    BaseSettingsDialog(title = "开源许可证", onDismiss = onDismiss) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).padding(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item { Text(text = "开源软件许可证", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
            item { Spacer(Modifier.height(16.dp)) }

            item { LicenseEntry("Apache License 2.0", """
                Licensed under the Apache License, Version 2.0 (the "License");
                you may not use this file except in compliance with the License.
                You may obtain a copy of the License at

                    http://www.apache.org/licenses/LICENSE-2.0

                Unless required by applicable law or agreed to in writing, software
                distributed under the License is distributed on an "AS IS" BASIS,
                WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
                See the License for the specific language governing permissions and
                limitations under the License.
            """.trimIndent()) }

            item { Spacer(Modifier.height(16.dp)) }

            item { LicenseEntry("MIT License", """
                Permission is hereby granted, free of charge, to any person obtaining a copy
                of this software and associated documentation files (the "Software"), to deal
                in the Software without restriction, including without limitation the rights
                to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
                copies of the Software, and to permit persons to whom the Software is
                furnished to do so, subject to the following conditions:

                The above copyright notice and this permission notice shall be included in all
                copies or substantial portions of the Software.

                THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
                IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
                FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
                AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
                LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
                OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
                SOFTWARE.
            """.trimIndent()) }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
}

@Composable
private fun LibraryItem(name: String, author: String, desc: String) {
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp)) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
            Text(text = "$author · $desc", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun LicenseEntry(license: String, body: String) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
        Column {
            Text(text = license, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
            HorizontalDivider()
            Text(text = body, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
        }
    }
}
