package com.kevinfreyap.jetspending.ui.screen.privacy_security

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.biometric.BiometricAuthManager
import com.kevinfreyap.jetspending.ui.components.ViewCustomDialog
import com.kevinfreyap.jetspending.ui.components.ViewPasswordDialog
import com.kevinfreyap.jetspending.ui.components.ViewPreferenceRow
import com.kevinfreyap.jetspending.ui.components.ViewTextField
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.state.PasswordFormState
import com.kevinfreyap.jetspending.ui.state.PrivacySecurityAction
import com.kevinfreyap.jetspending.ui.state.PrivacySecurityState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Red500
import com.kevinfreyap.jetspending.ui.theme.Theme
import kotlinx.coroutines.delay

@Composable
fun PrivacySecurityScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PrivacySecurityViewModel = hiltViewModel()
) {
    val privacySecurityState by viewModel.privacySecurityState.collectAsState()
    val passwordFormState by viewModel.passwordForm.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val authManager = remember { BiometricAuthManager(context) }

    val (showPasswordDialog, setShowPasswordDialog) = remember { mutableStateOf(false) }
    val (showFailureDialog, setShowFailureDialog) = remember { mutableStateOf(false) }

    val isChangingPassword = remember(showPasswordDialog) { privacySecurityState.hasPassword }

    PrivacySecurityContent(
        onBackClick = onBackClick,
        onShowPasswordDialog = {
            setShowPasswordDialog(true)
        },
        onDismissDialog = {
            setShowPasswordDialog(false)
            viewModel.onDismissDialog()
            viewModel.clearForm()
        },
        onShowFailDialog = { show ->
            if (show) {
                setShowPasswordDialog(false)
                viewModel.clearForm()
                setShowFailureDialog(true)
            } else {
                setShowFailureDialog(false)
            }
        },
        isChangingPassword = isChangingPassword,
        showPasswordDialog = showPasswordDialog,
        showSuccessDialog = showDialog,
        showFailureDialog = showFailureDialog,
        uiState = uiState,
        passwordFormState = passwordFormState,
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

            override fun onPasswordChange(pass: String) {
                viewModel.onPasswordChange(pass)
            }

            override fun onNewPasswordChange(pass: String) {
                viewModel.onNewPasswordChange(pass)
            }

            override fun onConfirmPasswordDialog() {
                if (isChangingPassword) {
                    viewModel.changePassword()
                } else {
                    viewModel.createPassword()
                }
            }

            override fun onCancelPasswordDialog() {
                viewModel.clearForm()
                setShowPasswordDialog(false)
            }

        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityContent(
    onBackClick: () -> Unit,
    onShowPasswordDialog: () -> Unit,
    onDismissDialog: () -> Unit,
    onShowFailDialog: (Boolean) -> Unit,
    isChangingPassword: Boolean,
    showPasswordDialog: Boolean,
    showSuccessDialog: Boolean,
    showFailureDialog: Boolean,
    uiState: UiState<Unit>,
    passwordFormState: PasswordFormState,
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
                title = if (privacySecurityState.hasPassword) stringResource(R.string.change_password) else stringResource(R.string.create_password),
                onClick = onShowPasswordDialog
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

        if (showPasswordDialog) {
            var showPassword by remember { mutableStateOf(false) }
            var showNewPassword by remember { mutableStateOf(false) }

            val passwordError = if (uiState is UiState.ValidationErrors) {
                uiState.errors[Field.AUTHENTICATION_PASSWORD]
            } else null

            val newPasswordError = if (uiState is UiState.ValidationErrors) {
                uiState.errors[Field.AUTHENTICATION_NEW_PASSWORD]
            } else null

            if (newPasswordError == R.string.error_relogin) {
                onShowFailDialog(true)
            }

            val isError = if (isChangingPassword) passwordError else newPasswordError

            val title: String
            val subtitle: String
            val value: String
            val onValueChange: (String) -> Unit
            val imeAction: ImeAction

            if (isChangingPassword){
                title = stringResource(R.string.change_password)
                subtitle = stringResource(R.string.description_change_password)
                value = passwordFormState.password
                onValueChange = privacySecurityAction::onPasswordChange
                imeAction = ImeAction.Next
            } else {
                title = stringResource(R.string.create_password)
                subtitle = stringResource(R.string.description_create_password)
                value = passwordFormState.newPassword
                onValueChange = privacySecurityAction::onNewPasswordChange
                imeAction = ImeAction.Done
            }

            ViewPasswordDialog(
                title = title,
                subtitle = subtitle,
                onDismissRequest = privacySecurityAction::onCancelPasswordDialog,
                isLoading = uiState is UiState.Loading,
                password = {
                    ViewTextField(
                        value = value,
                        onValueChange = onValueChange,
                        label = if (isChangingPassword) stringResource(R.string.current_password) else stringResource(R.string.new_password),
                        isError = isError != null,
                        errorMessage = isError?.let { stringResource(it) } ?: "",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = imeAction
                        ),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            if (value.isNotBlank()){
                                IconButton(
                                    onClick = {
                                        showPassword = !showPassword
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            if (showPassword) R.drawable.ic_visibility_24 else R.drawable.ic_visibility_off_24
                                        ),
                                        contentDescription = "Password Visibility Toggle"
                                    )
                                }
                            }
                        }
                    )
                },
                newPassword = {
                    if (isChangingPassword) {
                        ViewTextField(
                            value = passwordFormState.newPassword,
                            onValueChange = privacySecurityAction::onNewPasswordChange,
                            label = stringResource(R.string.new_password),
                            isError = newPasswordError != null,
                            errorMessage = newPasswordError?.let { stringResource(it) } ?: "",
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                if (passwordFormState.newPassword.isNotBlank()){
                                    IconButton(
                                        onClick = {
                                            showNewPassword = !showNewPassword
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                if (showNewPassword) R.drawable.ic_visibility_24 else R.drawable.ic_visibility_off_24
                                            ),
                                            contentDescription = "Password Visibility Toggle"
                                        )
                                    }
                                }
                            }
                        )
                    }
                },
                positiveBtn = {
                    Button(
                        onClick = privacySecurityAction::onConfirmPasswordDialog
                    ) {
                        Text(
                            text = stringResource(R.string.confirm),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                negativeBtn = {
                    Button(
                        onClick = privacySecurityAction::onCancelPasswordDialog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Grey500
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            )
        }

        if (showSuccessDialog) {
            LaunchedEffect(Unit) {
                delay(3000)
                onDismissDialog()
            }

            ViewCustomDialog(
                onDismissRequest = onDismissDialog,
                icon = R.drawable.ic_check_circle_outline_24,
                iconColor = Green500,
                title = stringResource(if (isChangingPassword) R.string.success_change_password else R.string.success_create_password) ,
            )
        }

        if (showFailureDialog) {
            LaunchedEffect(Unit) {
                delay(5000)
                onShowFailDialog(false)
            }

            ViewCustomDialog(
                onDismissRequest = { onShowFailDialog(false) },
                icon = R.drawable.ic_close_24,
                iconColor = Red500,
                title = stringResource(R.string.error_create_password),
                message = stringResource(R.string.error_relogin)
            )
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
            onShowPasswordDialog = {},
            onDismissDialog = {},
            onShowFailDialog = {},
            isChangingPassword = true,
            showPasswordDialog = true,
            showSuccessDialog = false,
            showFailureDialog = false,
            uiState = UiState.Loading,
            passwordFormState = PasswordFormState(),
            privacySecurityState = PrivacySecurityState(
                isAppLockEnabled = false,
                isBlockScreenshotEnabled = true
            ),
            privacySecurityAction = object: PrivacySecurityAction{
                override fun onAppLockSwitch(isEnabled: Boolean) {

                }

                override fun onBlockScreenSwitch(isEnabled: Boolean) {

                }

                override fun onPasswordChange(pass: String) {

                }

                override fun onNewPasswordChange(pass: String) {

                }

                override fun onConfirmPasswordDialog() {

                }

                override fun onCancelPasswordDialog() {

                }
            }
        )
    }
}