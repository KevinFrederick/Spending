package com.kevinfreyap.jetspending.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var uiState: MainActivityState by mutableStateOf(MainActivityState.Loading)
        val splashScreen = installSplashScreen()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState = it }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            uiState is MainActivityState.Loading
        }

        setContent {
            when (val state = uiState) {
                is MainActivityState.Loading -> {}
                is MainActivityState.Success -> {
                    val isDarkTheme = when(state.theme) {
                        AppTheme.LIGHT -> false
                        AppTheme.DARK -> true
                        AppTheme.SYSTEM -> isSystemInDarkTheme()
                    }

                    JetSpendingTheme (
                        darkTheme = isDarkTheme
                    ) {
                        Surface(
                            modifier = Modifier.Companion.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            JetSpendingApp(
                                startDestination = state.startDestination
                            )
                        }
                    }
                }
            }


        }
    }
}