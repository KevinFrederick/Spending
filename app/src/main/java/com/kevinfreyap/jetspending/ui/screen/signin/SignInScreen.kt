package com.kevinfreyap.jetspending.ui.screen.signin

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import com.kevinfreyap.jetspending.ui.action.SignInAction
import com.kevinfreyap.jetspending.ui.client.GoogleAuthClient
import com.kevinfreyap.jetspending.ui.components.ViewGoogleBtn
import com.kevinfreyap.jetspending.ui.components.ViewInputDialog
import com.kevinfreyap.jetspending.ui.components.ViewTextInputField
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.ViewAnnotatedTextButton
import com.kevinfreyap.jetspending.ui.components.ViewPrimaryButton
import com.kevinfreyap.jetspending.ui.navigation.SignInNavigation
import com.kevinfreyap.jetspending.ui.state.SignInState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.ui.state.UiEvent
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    onNavigate: (SignInNavigation) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val coroutineScope = rememberCoroutineScope()
    val googleAuthClient = remember { GoogleAuthClient(context) }

    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsStateWithLifecycle()

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

    SignInContent(
        state = state,
        onAction = viewModel::onAction,
        onGoogleBtnClicked = {
            coroutineScope.launch {
                val idToken = googleAuthClient.getGoogleIdToken()
                if (idToken != null) {
                    viewModel.onAction(SignInAction.OnGoogleAuthClicked(idToken))
                }
            }
        },
        showForgotPasswordDialog = showForgotPasswordDialog,
        onNavigate = onNavigate,
        onShowForgotPasswordDialog = {
            showForgotPasswordDialog = it
        },
        modifier = modifier
    )
}

@Composable
private fun SignInContent(
    state: SignInState,
    showForgotPasswordDialog: Boolean,
    onAction: (SignInAction) -> Unit,
    onGoogleBtnClicked: () -> Unit,
    onShowForgotPasswordDialog: (Boolean) -> Unit,
    onNavigate: (SignInNavigation) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var showPassword by remember { mutableStateOf(false) }

    val emailError = if (state.authStatus is UiState.ValidationErrors) {
        state.authStatus.errors[Field.AUTHENTICATION_EMAIL]
    } else null

    val passwordError = if (state.authStatus is UiState.ValidationErrors) {
        state.authStatus.errors[Field.AUTHENTICATION_PASSWORD]
    } else null

    val emailResetPasswordError = if (state.authStatus is UiState.ValidationErrors) {
        state.authStatus.errors[Field.AUTHENTICATION_CHANGE_PASSWORD_EMAIL]
    } else null

    Scaffold(
        topBar = {
            ViewTopBar(
                title = "",
                onBackClick = {
                    onNavigate(SignInNavigation.NavigateUp)
                },
                isLoading = state.authStatus is UiState.Loading
            )
        },
        bottomBar = {
            ViewAnnotatedTextButton(
                labelText = stringResource(R.string.annotated_label_do_not_have_account_label),
                textButton = stringResource(R.string.btn_sign_up),
                onClick = {
                    onNavigate(SignInNavigation.NavigateToSignUp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .navigationBarsPadding(),
            )
        }
    ) { innerPadding ->
        Column (
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                        }
                    )
                }
                .verticalScroll(rememberScrollState())
                .padding(
                    top = 24.dp,
                    bottom = 16.dp,
                    start = 24.dp,
                    end = 24.dp
                )
        ) {
            Text(
                text = stringResource(R.string.title_message_sign_in),
                style = MaterialTheme.typography.headlineLarge,
                color = Theme.custom.primaryTextColor
            )

            Text(
                text = stringResource(R.string.subtitle_message_sign_in),
                style = MaterialTheme.typography.bodyLarge,
                color = Theme.custom.secondaryTextColor
            )

            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )

            ViewTextInputField(
                value = state.emailInput,
                label = stringResource(R.string.label_email),
                onValueChange = {
                    onAction(SignInAction.OnEmailChanged(it))
                },
                isError = emailError != null,
                errorMessage = emailError?.let { stringResource(it) } ?: "",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            ViewTextInputField(
                value = state.passwordInput,
                onValueChange = {
                    onAction(SignInAction.OnPasswordChanged(it))
                },
                label = stringResource(R.string.label_password),
                isError = passwordError != null,
                errorMessage = passwordError?.let { stringResource(it) } ?: "",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    if (state.passwordInput.isNotBlank()){
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

            TextButton(
                onClick = {
                    onShowForgotPasswordDialog(true)
                },
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                Text(
                    text = stringResource(R.string.label_forgot_password),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )

            ViewPrimaryButton(
                text = stringResource(R.string.btn_sign_in),
                onClick = {
                    focusManager.clearFocus()
                    onAction(SignInAction.OnSignInClicked)
                },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(
                modifier = Modifier
                    .height(24.dp)
            )

            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ){
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .weight(1f)
                )

                Text(
                    text = stringResource(R.string.label_email_google_divider),
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor,
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp
                        )
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .weight(1f)
                )
            }

            Spacer(
                modifier = Modifier
                    .height(24.dp)
            )

            ViewGoogleBtn(
                onClick = onGoogleBtnClicked,
            )
        }

        if (showForgotPasswordDialog) {
            ViewInputDialog(
                title = stringResource(R.string.dialog_title_reset_password),
                subtitle = stringResource(R.string.description_reset_password),
                positiveText = stringResource(R.string.btn_send_link),
                onDismissRequest = {
                    onShowForgotPasswordDialog(false)
                },
                onPositiveClicked = {
                    onAction(SignInAction.OnSendResetEmail)
                },
                textField = {
                    ViewTextInputField(
                        value = state.resetPasswordEmailInput,
                        onValueChange = {
                            onAction(SignInAction.OnResetPasswordEmailChanged(it))
                        },
                        label = stringResource(R.string.label_email),
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
    backgroundColor = 0xFFFAFCFB,
    uiMode = UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    backgroundColor = 0xFF121214,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
private fun SignInContentPreview() {
    JetSpendingTheme {
        SignInContent(
            state = SignInState(),
            onAction = {},
            onGoogleBtnClicked = {},
            onNavigate = {},
            showForgotPasswordDialog = false,
            onShowForgotPasswordDialog = {},
        )
    }
}