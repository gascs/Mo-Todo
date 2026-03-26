package com.motut.mo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.motut.mo.ui.MainScreenV2
import com.motut.mo.ui.SplashScreen
import com.motut.mo.ui.theme.MoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        onBackPressedDispatcher.addCallback(this, backHandler)
        
        setContent {
            MoTheme {
                var showSplash by remember { mutableStateOf(true) }

                AnimatedContent(
                    targetState = showSplash,
                    label = "SplashTransition",
                    transitionSpec = {
                        fadeIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = FastOutSlowInEasing
                            )
                        ) togetherWith
                        fadeOut(
                            animationSpec = tween(
                                durationMillis = 300,
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
        }
    }
}
