package com.kevinfreyap.jetspending.ui.screen.privacy_security

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.v2.ViewTopBar
import com.kevinfreyap.jetspending.ui.screen.settings.components.SettingsSection
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsItemState
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsItemType
import com.kevinfreyap.jetspending.ui.action.PrivacySecurityAction
import com.kevinfreyap.jetspending.ui.action.PrivacySecurityAction.*
import com.kevinfreyap.jetspending.ui.biometric.BiometricAuthManager
import com.kevinfreyap.jetspending.ui.components.v2.ViewAppDialog
import com.kevinfreyap.jetspending.ui.components.v2.ViewInputDialog
import com.kevinfreyap.jetspending.ui.components.v2.ViewTextInputField
import com.kevinfreyap.jetspending.ui.model.DialogType
import com.kevinfreyap.jetspending.ui.navigation.PrivacySecurityNavigation
import com.kevinfreyap.jetspending.ui.screen.privacy_security.state.PrivacySecurityDialogType
import com.kevinfreyap.jetspending.ui.state.PrivacySecurityState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.UiEvent
import kotlinx.coroutines.launch

@Composable
fun PrivacySecurityScreen(
    onNavigate: (PrivacySecurityNavigation) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PrivacySecurityViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    val state by viewModel.state.collectAsStateWithLifecycle()

    val biometricAuthManager = remember(context) { BiometricAuthManager(context) }
    val snackBarHostState = remember { SnackbarHostState() }

    val titleAppLocked = stringResource(R.string.label_app_locked)
    val noScreenLock = stringResource(R.string.label_no_screen_lock)
    val settings = stringResource(R.string.settings)

    LaunchedEffect(viewModel.uiEvent, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvent.collect { event ->
                when(event) {
                    is UiEvent.Navigate -> {
                        onNavigate(event.destination)
                    }
                    is UiEvent.ShowToast -> {
                        Toast.makeText(
                            context,
                            event.asString(context),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    PrivacySecurityContent(
        state = state,
        onAction = { action ->
            if (action == OnToggleAppLock) {
                if (!state.isAppLockEnabled) {
                    biometricAuthManager.showBiometricPrompt(
                        title = titleAppLocked,
                        onSuccess = {
                            viewModel.onAction(action)
                        },
                        onNotEnrolled = {
                            scope.launch {
                                val result = snackBarHostState.showSnackbar(
                                    message = noScreenLock,
                                    actionLabel = settings,
                                    duration = SnackbarDuration.Long
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    context.startActivity(biometricAuthManager.createEnrollIntent())
                                }
                            }
                        },
                        onError = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    biometricAuthManager.showBiometricPrompt(
                        title = titleAppLocked,
                        onSuccess = {
                            viewModel.onAction(action)
                        },
                        onNotEnrolled = {
                            viewModel.onAction(action)
                        },
                        onError = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            } else {
                viewModel.onAction(action)
            }
        },
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@Composable
fun PrivacySecurityContent(
    state: PrivacySecurityState,
    onAction: (PrivacySecurityAction) -> Unit,
    onNavigate: (PrivacySecurityNavigation) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.security),
                onBackClick = {
                    onNavigate(
                        PrivacySecurityNavigation.NavigateUp
                    )
                },
                isLoading = state.uiState is UiState.Loading
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    top = 12.dp,
                    bottom = 16.dp,
                    start = 24.dp,
                    end = 24.dp
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_security_screen_header),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Theme.custom.primaryTextColor
                )

                Text(
                    text = stringResource(R.string.settings_security_screen_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.custom.secondaryTextColor
                )
            }


            Spacer(Modifier.height(32.dp))

            SettingsSection(
                header = R.string.settings_header_app_access,
                settings = listOf(
                    SettingsItemState(
                        type = SettingsItemType.APP_LOCK,
                        iconRes = R.drawable.ic_fingerprint_24,
                        name = R.string.settings_app_lock,
                        description = R.string.description_settings_app_lock,
                        isChecked = state.isAppLockEnabled
                    ),
                    SettingsItemState(
                        type = SettingsItemType.BLOCK_SCREENSHOT,
                        iconRes = R.drawable.ic_visibility_off_24,
                        name = R.string.settings_block_screenshot,
                        description = R.string.description_settings_block_screenshot,
                        isChecked = state.isScreenshotBlockEnabled
                    ),
                ),
                onItemClick = { settingsItem ->
                    when(settingsItem) {
                        SettingsItemType.APP_LOCK -> {
                            onAction(
                                OnToggleAppLock
                            )
                        }
                        SettingsItemType.BLOCK_SCREENSHOT -> {
                            onAction(
                                OnToggleBlockScreenshot
                            )
                        }
                        else -> Unit
                    }
                }
            )

            Spacer(Modifier.height(32.dp))

            SettingsSection(
                header = R.string.settings_header_account,
                settings = listOf(
                    SettingsItemState(
                        type = SettingsItemType.CHANGE_PASSWORD,
                        iconRes = R.drawable.ic_password_24,
                        name = if (state.hasPassword) R.string.settings_change_password else R.string.settings_create_password,
                        description = R.string.description_settings_password,
                    ),
                    SettingsItemState(
                        type = SettingsItemType.DELETE_ACCOUNT,
                        iconRes = R.drawable.ic_delete_24,
                        name = R.string.settings_delete_account,
                        description = R.string.description_settings_delete_account,
                    ),
                ),
                onItemClick = { settingsItem ->
                    when(settingsItem) {
                        SettingsItemType.CHANGE_PASSWORD -> {
                            onAction(
                                OnShowDialog(
                                    dialog = PrivacySecurityDialogType.CHANGE_PASSWORD
                                )
                            )
                        }
                        SettingsItemType.DELETE_ACCOUNT -> {
                            onAction(
                                OnShowDialog(
                                    dialog = PrivacySecurityDialogType.DELETE_ACCOUNT
                                )
                            )
                        }
                        else -> Unit
                    }
                }
            )
        }
    }

    when(state.activeDialog) {
        PrivacySecurityDialogType.NONE -> Unit
        PrivacySecurityDialogType.CHANGE_PASSWORD -> {
            val currentPasswordError = if (state.uiState is UiState.ValidationErrors) {
                state.uiState.errors[Field.AUTHENTICATION_PASSWORD]
            } else null

            val newPasswordError = if (state.uiState is UiState.ValidationErrors) {
                state.uiState.errors[Field.AUTHENTICATION_NEW_PASSWORD]
            } else null

            var showCurrentPassword by remember { mutableStateOf(false) }
            var showNewPassword by remember { mutableStateOf(false) }

            ViewInputDialog(
                title = stringResource(
                    if (state.hasPassword) R.string.settings_change_password else R.string.settings_create_password
                ),
                subtitle = stringResource(R.string.description_settings_password),
                positiveText = stringResource(R.string.confirm),
                onDismissRequest = {
                    onAction(
                        OnShowDialog(
                            dialog = PrivacySecurityDialogType.NONE
                        )
                    )
                },
                textField = {
                    Column {
                        if (state.hasPassword) {
                            ViewTextInputField(
                                value = state.passwordForm.password,
                                onValueChange = {
                                    onAction(
                                        OnPasswordChange(
                                            current = it,
                                            new = state.passwordForm.newPassword
                                        )
                                    )
                                },
                                label = stringResource(R.string.current_password),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                isError = currentPasswordError != null,
                                errorMessage = currentPasswordError?.let { stringResource(it) } ?: "",
                                trailingIcon = {
                                    if (state.passwordForm.password.isNotBlank()){
                                        IconButton(
                                            onClick = {
                                                showCurrentPassword = !showCurrentPassword
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(
                                                    if (showCurrentPassword) R.drawable.ic_visibility_24 else R.drawable.ic_visibility_off_24
                                                ),
                                                contentDescription = "Password Visibility Toggle"
                                            )
                                        }
                                    }
                                }
                            )

                            Spacer(Modifier.height(8.dp))
                        }

                        ViewTextInputField(
                            value = state.passwordForm.newPassword,
                            onValueChange = {
                                onAction(
                                    OnPasswordChange(
                                        current = state.passwordForm.password,
                                        new = it
                                    )
                                )
                            },
                            label = stringResource(R.string.new_password),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = newPasswordError != null,
                            errorMessage = newPasswordError?.let { stringResource(it) } ?: "",
                            trailingIcon = {
                                if (state.passwordForm.newPassword.isNotBlank()){
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

                        if (state.hasPassword) {
                            TextButton(
                                onClick = {
                                    onAction(
                                        OnShowDialog(
                                            dialog = PrivacySecurityDialogType.FORGOT_PASSWORD
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                            ) {
                                Text(
                                    text = stringResource(R.string.forgot_password),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                },
                onPositiveClicked = {
                    onAction(
                        OnSubmitPassword
                    )
                }
            )
        }
        PrivacySecurityDialogType.DELETE_ACCOUNT -> {
            if (state.hasPassword) {
                var showDeletePassword by remember { mutableStateOf(false) }

                val currentPasswordError = if (state.uiState is UiState.ValidationErrors) {
                    state.uiState.errors[Field.AUTHENTICATION_PASSWORD]
                } else null

                ViewInputDialog(
                    title = stringResource(R.string.settings_delete_account),
                    subtitle = stringResource(R.string.description_settings_delete_account),
                    positiveText = stringResource(R.string.delete_account),
                    positiveBtnContainerColor = Theme.custom.negativeContainer,
                    positiveBtnContentColor = Theme.custom.statusNegative,
                    onDismissRequest = {
                        onAction(OnShowDialog(PrivacySecurityDialogType.NONE))
                    },
                    textField = {
                        ViewTextInputField(
                            value = state.passwordForm.password,
                            onValueChange = {
                                onAction(
                                    OnPasswordChange(
                                        current = it,
                                        new = state.passwordForm.newPassword
                                    )
                                )
                            },
                            label = stringResource(R.string.current_password),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            visualTransformation = if (showDeletePassword) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = currentPasswordError != null,
                            errorMessage = currentPasswordError?.let { stringResource(it) } ?: "",
                            trailingIcon = {
                                if (state.passwordForm.password.isNotBlank()){
                                    IconButton(
                                        onClick = {
                                            showDeletePassword = !showDeletePassword
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(
                                                if (showDeletePassword) R.drawable.ic_visibility_24 else R.drawable.ic_visibility_off_24
                                            ),
                                            contentDescription = "Password Visibility Toggle"
                                        )
                                    }
                                }
                            }
                        )
                    },
                    onPositiveClicked = {
                        onAction(OnConfirmDelete)
                    }
                )
            } else {
                ViewAppDialog(
                    dialogType = DialogType.DESTRUCTIVE,
                    title = stringResource(R.string.settings_delete_account),
                    message = stringResource(R.string.description_settings_delete_account),
                    confirmText = stringResource(R.string.delete_account),
                    dismissText = stringResource(R.string.cancel),
                    onConfirm = {
                        onAction(
                            OnConfirmDelete
                        )
                    },
                    onDismissRequest = {
                        onAction(
                            OnShowDialog(
                                PrivacySecurityDialogType.NONE
                            )
                        )
                    }
                )
            }
        }

        PrivacySecurityDialogType.FORGOT_PASSWORD -> {
            val emailResetPasswordError = if (state.uiState is UiState.ValidationErrors) {
                state.uiState.errors[Field.AUTHENTICATION_CHANGE_PASSWORD_EMAIL]
            } else null

            ViewInputDialog(
                title = stringResource(R.string.reset_password),
                subtitle = stringResource(R.string.description_reset_password),
                positiveText = stringResource(R.string.send_link),
                onDismissRequest = {
                    onAction(
                        OnShowDialog(
                            PrivacySecurityDialogType.NONE
                        )
                    )
                },
                onPositiveClicked = {
                    onAction(
                        OnSendResetEmail
                    )
                },
                textField = {
                    ViewTextInputField(
                        value = state.resetEmail,
                        onValueChange = {
                            onAction(
                                OnResetEmailChange(it)
                            )
                        },
                        label = stringResource(R.string.email),
                        isError = emailResetPasswordError != null,
                        errorMessage = emailResetPasswordError?.let { stringResource(it) } ?: "",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        )
                    )
                },
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
            state = PrivacySecurityState(
                activeDialog = PrivacySecurityDialogType.NONE
            ),
            onAction = {},
            onNavigate = {}
        )
    }
}