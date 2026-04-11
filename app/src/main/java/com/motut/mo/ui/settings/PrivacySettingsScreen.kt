package com.motut.mo.ui.settings

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    onDismiss: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToUserAgreement: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPreferences = remember { (context.applicationContext as com.motut.mo.MoApplication).userPreferences }
    val appLockEnabled by userPreferences.appLockEnabled.collectAsState(initial = false)
    val showAnnouncement by userPreferences.showAnnouncement.collectAsState(initial = true)
    var lockApp by remember { mutableStateOf(appLockEnabled) }
    var hideContent by remember { mutableStateOf(false) }
    var showBiometricPrompt by remember { mutableStateOf(false) }

    LaunchedEffect(appLockEnabled) { lockApp = appLockEnabled }

    val biometricManager = remember { BiometricManager.from(context) }
    val canAuthenticate = remember {
        biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
    }

    if (showBiometricPrompt && canAuthenticate) {
        LaunchedEffect(Unit) {
            val activity = context as? FragmentActivity ?: return@LaunchedEffect
            val executor = ContextCompat.getMainExecutor(activity)
            val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    lockApp = true
                    scope.launch { userPreferences.setAppLockEnabled(true) }
                    showBiometricPrompt = false
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    lockApp = false
                    showBiometricPrompt = false
                }
            })
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("验证身份")
                .setSubtitle("请验证身份以启用应用锁")
                .setNegativeButtonText("取消")
                .build()
            prompt.authenticate(promptInfo)
        }
    }

    BaseSettingsDialog(title = "隐私与安全", onDismiss = onDismiss) { paddingValues ->
        SettingsContentColumn(paddingValues) {
            SettingsCard { Column {
                SettingSwitchItem(title = "应用锁", description = "使用生物识别保护应用",
                    checked = lockApp, onCheckedChange = {
                        if (it && canAuthenticate) showBiometricPrompt = true else {
                            lockApp = false; scope.launch { userPreferences.setAppLockEnabled(false) }
                        }
                    })
                HorizontalDivider()
                SettingSwitchItem(title = "隐藏通知内容", description = "在锁定屏幕上隐藏详细内容",
                    checked = hideContent, onCheckedChange = { hideContent = it })
                HorizontalDivider()
                SettingSwitchItem(title = "显示公告", description = "接收应用的更新公告",
                    checked = showAnnouncement, onCheckedChange = { scope.launch { userPreferences.setShowAnnouncement(it) } })
            }}
            SettingsCard { Column {
                SettingItem(title = "隐私政策", description = "了解我们如何保护您的隐私",
                    icon = Icons.Default.Policy, onClick = onNavigateToPrivacyPolicy)
                HorizontalDivider()
                SettingItem(title = "用户协议", description = "查看服务条款和协议",
                    icon = Icons.Default.Description, onClick = onNavigateToUserAgreement)
            }}
        }
    }
}
