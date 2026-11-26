package com.kevinfreyap.jetspending.ui.main

import android.content.Intent
import android.os.Build
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.domain.notification.NotificationConst
import com.kevinfreyap.jetspending.ui.biometric.AppLockedScreen
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.utils.findActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleWeeklySummaryIntent(intent)

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
                        onUnlocked = {
                            viewModel.onEvent(MainEvent.SetUnlocked(true))
                        },
                        onDisableAppLockAndUnlock = {
                            viewModel.onEvent(MainEvent.DisableAppLockAndUnlock)
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleWeeklySummaryIntent(intent)
    }

    private fun handleWeeklySummaryIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(NotificationConst.EXTRA_OPEN_WEEKLY_SUMMARY, false) == true) {
            intent.removeExtra(NotificationConst.EXTRA_OPEN_WEEKLY_SUMMARY)
            viewModel.onEvent(MainEvent.OpenWeeklySummaryReport)
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            viewModel.onEvent(MainEvent.AppBackgrounded)
        }
    }
}

@Composable
private fun MainContent(
    state: MainActivityState.Success,
    effects: Flow<MainEffect>,
    onUnlocked: () -> Unit,
    onDisableAppLockAndUnlock: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember (context) { context.findActivity() }

    DisposableEffect(activity, state.isSecureScreenEnabled) {
        val window = activity?.window

        if (window != null) {
            if (state.isSecureScreenEnabled) {
                window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    activity.setRecentsScreenshotEnabled(false)
                }
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    activity.setRecentsScreenshotEnabled(true)
                }
            }
        }
        onDispose {}
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
                    AppLockedScreen (
                        onUnlocked = onUnlocked,
                        onDisableAppLockAndUnlock = onDisableAppLockAndUnlock
                    )
                }
            }
        }
    }
}