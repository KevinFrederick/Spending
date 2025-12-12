package com.kevinfreyap.jetspending.ui.screen.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ViewCustomDialog
import com.kevinfreyap.jetspending.ui.components.ViewGoogleBtn
import com.kevinfreyap.jetspending.ui.components.ViewTextField
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.model.UiState
import com.kevinfreyap.jetspending.ui.theme.Blue500
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.Grey700
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import kotlinx.coroutines.delay

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignInClicked: () -> Unit,
    navigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val showDialog by viewModel.showDialog.collectAsState()

    SignUpContent(
        uiState = uiState,
        onBackClick = onBackClick,
        email = email,
        onEmailChange = {
            viewModel.onEmailChange(it)
        },
        password = password,
        onPasswordChange = {
            viewModel.onPassChange(it)
        },
        confirmPassword = confirmPassword,
        onConfirmPasswordChange = {
            viewModel.onConfirmPassChange(it)
        },
        onSignUpClicked = {
            viewModel.onSignUpClicked()
        },
        onSignInClicked = onSignInClicked,
        showSuccessDialog = showDialog,
        onDismissDialog = {
            viewModel.onDismissDialog()
            navigateToDashboard()
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpContent(
    uiState: UiState<Unit>,
    onBackClick: () -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    onSignUpClicked: () -> Unit,
    onSignInClicked: () -> Unit,
    showSuccessDialog: Boolean,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    val emailError = if (uiState is UiState.ValidationErrors) {
        uiState.errors[Field.AUTHENTICATION_EMAIL]
    } else null

    val passwordError = if (uiState is UiState.ValidationErrors) {
        uiState.errors[Field.AUTHENTICATION_PASSWORD]
    } else null

    val confirmPasswordError = if (uiState is UiState.ValidationErrors) {
        uiState.errors[Field.AUTHENTICATION_CONFIRM_PASSWORD]
    } else null

    Scaffold(
        topBar = {
            ViewTopBar(
                title = "",
                onBackClick = onBackClick,
                isLoading = uiState is UiState.Loading
            )
        }
    ) { innerPadding ->
        Column (
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    top = 24.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.sign_up),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = stringResource(R.string.description_sign_up),
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )

            ViewTextField(
                value = email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.email),
                isError = emailError != null,
                errorMessage = emailError?.let { stringResource(it) } ?: "",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            ViewTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.password),
                isError = passwordError != null,
                errorMessage = passwordError?.let { stringResource(it) } ?: "",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    if (password.isNotBlank()){
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

            ViewTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = stringResource(R.string.confirm_password),
                isError = confirmPasswordError != null,
                errorMessage = confirmPasswordError?.let { stringResource(it) } ?: "",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    if (confirmPassword.isNotBlank()){
                        IconButton(
                            onClick = {
                                showConfirmPassword = !showConfirmPassword
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (showConfirmPassword) R.drawable.ic_visibility_24 else R.drawable.ic_visibility_off_24
                                ),
                                contentDescription = "Confirm Password Visibility Toggle"
                            )
                        }
                    }
                }
            )

            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSignUpClicked()
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.sign_up),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(
                            vertical = 8.dp
                        )
                )
            }

            Spacer(
                modifier = Modifier
                    .height(42.dp)
            )

            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ){
                HorizontalDivider(
                    color = Grey700,
                    modifier = Modifier
                        .weight(1f)
                )

                Text(
                    text = stringResource(R.string.email_google_divider),
                    style = MaterialTheme.typography.bodySmall,
                    color = Grey700,
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp
                        )
                )

                HorizontalDivider(
                    color = Grey700,
                    modifier = Modifier
                        .weight(1f)
                )
            }

            Spacer(
                modifier = Modifier
                    .height(42.dp)
            )

            ViewGoogleBtn(
                modifier = Modifier
                    .padding(
                        horizontal = 64.dp
                    )
            )

            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )

            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.have_account_already))
                    append(" ")
                    withStyle(style = SpanStyle(
                        color = Blue500,
                        fontWeight = FontWeight.SemiBold
                    )) {
                        append(stringResource(R.string.sign_in))
                    }
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .clickable {
                        onSignInClicked()
                    }
                    .fillMaxWidth()
            )
        }

        if (showSuccessDialog) {
            LaunchedEffect(Unit) {
                delay(2000)
                onDismissDialog()
            }

            ViewCustomDialog(
                onDismissRequest = onDismissDialog,
                icon = R.drawable.ic_check_circle_outline_24,
                iconColor = Green500,
                title = stringResource(R.string.success_login_success),
                message = stringResource(R.string.success_message_welcome_back)
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO
)
@Composable
fun SignUpContentPreview() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    JetSpendingTheme {
        SignUpContent(
            uiState = UiState.Idle,
            onBackClick = {},
            email = email,
            onEmailChange = {
                email = it
            },
            password = password,
            onPasswordChange = {
                password = it
            },
            confirmPassword = confirmPassword,
            onConfirmPasswordChange = {
                confirmPassword = it
            },
            onSignUpClicked = {},
            onSignInClicked = {},
            showSuccessDialog = false,
            onDismissDialog = {},
        )
    }
}