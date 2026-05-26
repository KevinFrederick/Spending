package com.kevinfreyap.jetspending.ui.screen.privacy_security

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.biometric.BiometricAuthManager
import com.kevinfreyap.jetspending.ui.components.ViewPreferenceRow
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.state.PrivacySecurityAction
import com.kevinfreyap.jetspending.ui.state.PrivacySecurityState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Red500
import com.kevinfreyap.jetspending.ui.theme.Theme

@Composable
fun PrivacySecurityScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PrivacySecurityViewModel = hiltViewModel()
) {
    val privacySecurityState by viewModel.privacySecurityState.collectAsState()
    val context = LocalContext.current
    val authManager = remember { BiometricAuthManager(context) }

    PrivacySecurityContent(
        onBackClick = onBackClick,
        privacySecurityState = privacySecurityState,
        privacySecurityAction = object: PrivacySecurityAction {
            override fun onAppLockSwitch(isEnabled: Boolean) {
                if (isEnabled) {
                    if (authManager.isBiometricReady()) {
                        viewModel.toggleAppLock(true)
                    } else {
                        Toast.makeText(
                            context,
                            R.string.error_no_biometric,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    viewModel.toggleAppLock(false)
                }
            }

            override fun onBlockScreenSwitch(isEnabled: Boolean) {
                viewModel.toggleBlockScreen(isEnabled)
            }

        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityContent(
    onBackClick: () -> Unit,
    privacySecurityState: PrivacySecurityState,
    privacySecurityAction: PrivacySecurityAction,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.security),
                onBackClick = onBackClick,
                onSelectCurrency = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    top = 32.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            Text(
                text = stringResource(R.string.secure_access),
                style = MaterialTheme.typography.headlineSmall,
                color = Theme.custom.textColor
            )

            Spacer(
                modifier = Modifier
                    .height(4.dp)
            )

            Text(
                text = stringResource(R.string.description_secure_access),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                color = Theme.custom.textColor
            )

            Spacer(
                modifier = Modifier
                    .height(24.dp)
            )

            ViewPreferenceRow(
                title = stringResource(R.string.app_lock),
                subtitle = stringResource(R.string.app_lock_subtitle)
            ) {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    Switch(
                        checked = privacySecurityState.isAppLockEnabled,
                        colors = SwitchDefaults.colors(
                            uncheckedBorderColor = Theme.custom.hintColor,
                            uncheckedTrackColor = Theme.custom.nestedCardColor,
                            uncheckedThumbColor = Theme.custom.iconColor
                        ),
                        onCheckedChange = { isChecked ->
                            privacySecurityAction.onAppLockSwitch(isChecked)
                        },
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            ViewPreferenceRow(
                title = stringResource(R.string.block_screenshot),
                subtitle = stringResource(R.string.block_screenshot_subtitle)
            ) {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    Switch(
                        checked = privacySecurityState.isBlockScreenshotEnabled,
                        colors = SwitchDefaults.colors(
                            uncheckedBorderColor = Theme.custom.hintColor,
                            uncheckedTrackColor = Theme.custom.nestedCardColor,
                            uncheckedThumbColor = Theme.custom.iconColor
                        ),
                        onCheckedChange = { isChecked ->
                            privacySecurityAction.onBlockScreenSwitch(isChecked)
                        },
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .height(24.dp)
            )

            Text(
                text = stringResource(R.string.account_security),
                style = MaterialTheme.typography.headlineSmall,
                color = Theme.custom.textColor
            )

            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )

            Text(
                text = stringResource(R.string.description_account_security),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                color = Theme.custom.textColor
            )

            Spacer(
                modifier = Modifier
                    .height(24.dp)
            )

            ViewPreferenceRow(
                title = stringResource(R.string.change_password)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = Theme.custom.iconColor
                )
            }

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            ViewPreferenceRow(
                title = stringResource(R.string.delete_account),
                contentColor = Red500
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = Red500
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun PrivacySecurityPreview() {
    JetSpendingTheme {
        PrivacySecurityContent(
            onBackClick = {},
            privacySecurityState = PrivacySecurityState(
                isAppLockEnabled = false,
                isBlockScreenshotEnabled = true
            ),
            privacySecurityAction = object: PrivacySecurityAction{
                override fun onAppLockSwitch(isEnabled: Boolean) {

                }

                override fun onBlockScreenSwitch(isEnabled: Boolean) {

                }
            }
        )
    }
}