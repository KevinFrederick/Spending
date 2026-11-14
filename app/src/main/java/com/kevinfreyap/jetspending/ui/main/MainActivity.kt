package com.kevinfreyap.jetspending.ui.main

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.biometric.AppLockedFallbackScreen
import com.kevinfreyap.jetspending.ui.biometric.BiometricAuthManager
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var authManager: BiometricAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val splashScreen = installSplashScreen()
        authManager = BiometricAuthManager(this)

        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value is MainActivityState.Loading
        }

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            when (val state = uiState) {
                is MainActivityState.Loading -> Unit
                is MainActivityState.Success -> {
                    MainContent(
                        state = state,
                        effects = viewModel.effects,
                        onRequestBiometric = { triggerBiometricPrompt(state) }
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            viewModel.onEvent(MainEvent.AppBackgrounded)
        }
    }

    private fun triggerBiometricPrompt(state: MainActivityState.Success) {
        if (!state.isAppLockEnabled || state.isUnlocked) return

        if (authManager.isBiometricReady()) {
            authManager.showBiometricPrompt(
                title = getString(R.string.app_locked),
                onSuccess = { viewModel.onEvent(MainEvent.SetUnlocked(true)) },
                onError = {}
            )
        } else {
            viewModel.onEvent(MainEvent.EmergencyLogout)
        }
    }
}

@Composable
private fun MainContent(
    state: MainActivityState.Success,
    effects: Flow<MainEffect>,
    onRequestBiometric: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(state.isUnlocked) {
        if (!state.isUnlocked) {
            onRequestBiometric()
        }
    }

    LaunchedEffect(state.isSecureScreenEnabled) {
        val window = (context as? Activity)?.window ?: return@LaunchedEffect
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
        CompositionLocalProvider(
            LocalBaseCurrency provides state.selectedCurrency
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (state.isUnlocked) {
                    JetSpendingApp(
                        startDestination = state.startDestination,
                        isOnline = state.isOnline,
                        effects = effects
                    )
                } else {
                    AppLockedFallbackScreen (
                        onUnlockClick = onRequestBiometric
                    )
                }
            }
        }
    }
}