package com.kevinfreyap.jetspending.ui.biometric

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ViewBrandName
import com.kevinfreyap.jetspending.ui.components.ViewPrimaryButton
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun AppLockedScreen(
    onUnlocked: () -> Unit,
    onDisableAppLockAndUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val biometricAuthManager = remember(context) { BiometricAuthManager(context) }
    val promptTitle = stringResource(R.string.label_app_lock)

    val currentOnUnlocked by rememberUpdatedState(onUnlocked)
    val currentOnDisableAndUnlock by rememberUpdatedState(onDisableAppLockAndUnlock)

    val triggerUnlock = remember(biometricAuthManager, promptTitle) {
        {
            biometricAuthManager.showBiometricPrompt(
                title = promptTitle,
                onSuccess = { currentOnUnlocked() },
                onNotEnrolled = { currentOnDisableAndUnlock() },
                onCancel = {  },
                onError = { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        triggerUnlock()
    }

    AppLockedContent(
        onUnlockClick = triggerUnlock,
        modifier = modifier
    )
}

@Composable
fun AppLockedContent(
    onUnlockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewBrandName(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
            )
        },
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 24.dp
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(Theme.custom.highlightContainer)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_fingerprint_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(52.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.title_message_locked),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Theme.custom.primaryTextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Text(
                        text = stringResource(R.string.subtitle_message_locked),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Theme.custom.secondaryTextColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ViewPrimaryButton(
                    text = stringResource(R.string.btn_unlock_biometric),
                    onClick = onUnlockClick,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_fingerprint_24),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.hint_locked_screen),
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun AppLockedScreenPreview() {
    JetSpendingTheme {
        AppLockedContent (
            onUnlockClick = {}
        )
    }
}