package com.kevinfreyap.jetspending.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            viewModel.startDestination.value == null
        }

        setContent {
            JetSpendingTheme {
                val startRoute by viewModel.startDestination.collectAsStateWithLifecycle()

                Surface(
                    modifier = Modifier.Companion.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    startRoute?.let {
                        JetSpendingApp(
                            startDestination = it
                        )
                    }
                }
            }
        }
    }
}