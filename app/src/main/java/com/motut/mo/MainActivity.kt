package com.motut.mo

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.motut.mo.data.ThemeMode
import com.motut.mo.ui.MainScreenV2
import com.motut.mo.ui.SplashScreen
import com.motut.mo.ui.theme.MoTheme
import com.motut.mo.util.AnimationUtils
import com.motut.mo.util.Announcement
import com.motut.mo.util.AnnouncementFetcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    
    private var backPressedOnce = false
    private val backHandler = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (backPressedOnce) {
                finish()
                return
            }
            
            backPressedOnce = true
            lifecycleScope.launch {
                delay(2000)
                backPressedOnce = false
            }
        }
    }
    
    private var isAuthenticated = false
    private var authPromptShown = false
    
    private fun showBiometricPrompt(
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            }
        )

        val biometricManager = BiometricManager.from(this)
        val canUseDeviceCredential = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) == BiometricManager.BIOMETRIC_SUCCESS

        val promptInfo = if (canUseDeviceCredential) {
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("验证身份")
                .setSubtitle("请验证您的身份以继续使用")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                .build()
        } else {
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("验证身份")
                .setSubtitle("请验证您的身份以继续使用")
                .setNegativeButtonText("取消")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build()
        }

        biometricPrompt.authenticate(promptInfo)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        onBackPressedDispatcher.addCallback(this, backHandler)
        
        window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
        window.setPreferMinimalPostProcessing(true)
        
        setContent {
            val userPreferences = remember { (application as MoApplication).userPreferences }
            val themeMode by userPreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM.name)
            val customPrimaryColor by userPreferences.customPrimaryColor.collectAsState(initial = 0)
            val useDynamicColor by userPreferences.useDynamicColor.collectAsState(initial = true)
            val backgroundImageUri by userPreferences.backgroundImageUri.collectAsState(initial = "")
            val appLockEnabled by userPreferences.appLockEnabled.collectAsState(initial = false)
            val showAnnouncement by userPreferences.showAnnouncement.collectAsState(initial = true)
            val isSystemDark = isSystemInDarkTheme()
            
            val useDarkTheme = when (ThemeMode.valueOf(themeMode)) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemDark
            }

            var authState by remember { mutableStateOf(!appLockEnabled || isAuthenticated) }
            var showSplash by remember { mutableStateOf(true) }
            var announcement by remember { mutableStateOf<Announcement?>(null) }
            var showAnnouncementDialog by remember { mutableStateOf(false) }

            LaunchedEffect(appLockEnabled) {
                if (appLockEnabled && !isAuthenticated && !authPromptShown) {
                    authPromptShown = true
                    showBiometricPrompt(
                        onSuccess = {
                            isAuthenticated = true
                            authState = true
                        },
                        onError = {
                            finish()
                        }
                    )
                }
            }
            
            LaunchedEffect(showSplash, showAnnouncement) {
                if (!showSplash && showAnnouncement) {
                    announcement = AnnouncementFetcher.fetchAnnouncement()
                    if (announcement != null) {
                        showAnnouncementDialog = true
                    }
                }
            }

            if (authState || !appLockEnabled) {
                MoTheme(
                    darkTheme = useDarkTheme,
                    dynamicColor = useDynamicColor,
                    customPrimaryColor = customPrimaryColor
                ) {
                    AnimatedContent(
                        targetState = showSplash,
                        label = "SplashTransition",
                        transitionSpec = {
                            fadeIn(
                                animationSpec = tween(
                                    durationMillis = AnimationUtils.FAST_ANIMATION_DURATION,
                                    easing = FastOutSlowInEasing
                                )
                            ) togetherWith
                            fadeOut(
                                animationSpec = tween(
                                    durationMillis = AnimationUtils.FAST_ANIMATION_DURATION,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }
                    ) { isSplash ->
                        if (isSplash) {
                            SplashScreen(
                                onSplashFinished = { showSplash = false }
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (backgroundImageUri.isNotEmpty()) {
                                    val alpha = if (useDarkTheme) 0.15f else 0.3f
                                    val blurModifier = if (!useDarkTheme) {
                                        Modifier.blur(radius = 8.dp)
                                    } else {
                                        Modifier
                                    }
                                    Image(
                                        painter = rememberAsyncImagePainter(backgroundImageUri),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize().then(blurModifier),
                                        contentScale = ContentScale.Crop,
                                        alpha = alpha
                                    )
                                }
                                MainScreenV2(
                                    onBackPressed = { canGoBack ->
                                        if (canGoBack) {
                                            backPressedOnce = false
                                        } else {
                                            backHandler.handleOnBackPressed()
                                        }
                                    }
                                )
                            }
                        }
                    }
                    
                    if (showAnnouncementDialog && announcement != null) {
                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = { showAnnouncementDialog = false },
                            title = {
                                if (announcement?.title?.isNotEmpty() == true) {
                                    Text(announcement!!.title)
                                }
                            },
                            text = {
                                Text(announcement!!.content)
                            },
                            confirmButton = {
                                androidx.compose.material3.TextButton(onClick = { showAnnouncementDialog = false }) {
                                    Text("确定")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
