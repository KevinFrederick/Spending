package com.kevinfreyap.jetspending.ui.main

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.biometric.AppLockedFallbackScreen
import com.kevinfreyap.jetspending.ui.biometric.BiometricAuthManager
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val splashScreen = installSplashScreen()
        val authManager = BiometricAuthManager(this)

        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value is MainActivityState.Loading
        }

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            when (val state = uiState) {
                is MainActivityState.Loading -> {}
                is MainActivityState.Success -> {
                    val promptBiometric = {
                        if (state.isAppLockEnabled && !state.isUnlocked && authManager.isBiometricReady()) {
                            authManager.showBiometricPrompt(
                                title = getString(R.string.app_locked),
                                onSuccess = {
                                    viewModel.setUnlocked(true)
                                },
                                onError = {}
                            )
                        } else {
                            viewModel.emergencyLogout()
                        }
                    }

                    LaunchedEffect(state.isUnlocked) {
                        if (!state.isUnlocked) {
                            promptBiometric()
                        }
                    }

                    LaunchedEffect(state.isSecureScreenEnabled) {
                        if (state.isSecureScreenEnabled) {
                            window.setFlags(
                                WindowManager.LayoutParams.FLAG_SECURE,
                                WindowManager.LayoutParams.FLAG_SECURE
                            )
                        } else {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                        }
                    }

                    val isDarkTheme = when(state.theme) {
                        AppTheme.LIGHT -> false
                        AppTheme.DARK -> true
                        AppTheme.SYSTEM -> isSystemInDarkTheme()
                    }

                    JetSpendingTheme (
                        darkTheme = isDarkTheme
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            if (state.isUnlocked) {
                                JetSpendingApp(
                                    startDestination = state.startDestination
                                )
                            } else {
                                AppLockedFallbackScreen {
                                    promptBiometric()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.setUnlocked(false)
    }
}