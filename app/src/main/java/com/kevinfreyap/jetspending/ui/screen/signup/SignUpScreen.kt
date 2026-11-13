package com.kevinfreyap.jetspending.ui.screen.signup

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.kevinfreyap.jetspending.ui.action.SignUpAction
import com.kevinfreyap.jetspending.ui.client.GoogleAuthClient
import com.kevinfreyap.jetspending.ui.components.ViewGoogleBtn
import com.kevinfreyap.jetspending.ui.components.ViewTextInputField
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.v2.ViewAnnotatedTextButton
import com.kevinfreyap.jetspending.ui.components.v2.ViewPrimaryButton
import com.kevinfreyap.jetspending.ui.navigation.SignUpNavigation
import com.kevinfreyap.jetspending.ui.state.SignUpState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.UiEvent
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    onNavigate: (SignUpNavigation) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val coroutineScope = rememberCoroutineScope()
    val googleAuthClient = remember { GoogleAuthClient(context) }

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

    SignUpContent(
        state = state,
        onAction = viewModel::onAction,
        onNavigate = onNavigate,
        onGoogleClicked = {
            coroutineScope.launch {
                val idToken = googleAuthClient.getGoogleIdToken()
                if (idToken != null) {
                    viewModel.onAction(SignUpAction.OnGoogleAuthClicked(idToken))
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun SignUpContent(
    state: SignUpState,
    onAction: (SignUpAction) -> Unit,
    onNavigate: (SignUpNavigation) -> Unit,
    onGoogleClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    val emailError = if (state.authStatus is UiState.ValidationErrors) {
        state.authStatus.errors[Field.AUTHENTICATION_EMAIL]
    } else null

    val passwordError = if (state.authStatus is UiState.ValidationErrors) {
        state.authStatus.errors[Field.AUTHENTICATION_PASSWORD]
    } else null

    val confirmPasswordError = if (state.authStatus is UiState.ValidationErrors) {
        state.authStatus.errors[Field.AUTHENTICATION_CONFIRM_PASSWORD]
    } else null

    Scaffold(
        topBar = {
            ViewTopBar(
                title = "",
                onBackClick = {
                    onNavigate(SignUpNavigation.NavigateUp)
                },
                isLoading = state.authStatus is UiState.Loading
            )
        },
        bottomBar = {
            ViewAnnotatedTextButton(
                labelText = stringResource(R.string.have_account_already),
                textButton = stringResource(R.string.btn_sign_in),
                onClick = {
                    onNavigate(SignUpNavigation.NavigateToSignIn)
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
                .padding(
                    top = 24.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.title_message_sign_up),
                style = MaterialTheme.typography.headlineLarge,
                color = Theme.custom.primaryTextColor
            )

            Text(
                text = stringResource(R.string.subtitle_message_sign_up),
                style = MaterialTheme.typography.bodyLarge,
                color = Theme.custom.secondaryTextColor
            )

            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ViewTextInputField(
                    value = state.email,
                    onValueChange = {
                        onAction(SignUpAction.OnEmailChanged(it))
                    },
                    label = stringResource(R.string.email),
                    isError = emailError != null,
                    errorMessage = emailError?.let { stringResource(it) } ?: "",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                ViewTextInputField(
                    value = state.password,
                    onValueChange = {
                        onAction(SignUpAction.OnPasswordChanged(it))
                    },
                    label = stringResource(R.string.password),
                    isError = passwordError != null,
                    errorMessage = passwordError?.let { stringResource(it) } ?: "",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        if (state.password.isNotBlank()){
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

                ViewTextInputField(
                    value = state.confirmPassword,
                    onValueChange = {
                        onAction(SignUpAction.OnConfirmPasswordChanged(it))
                    },
                    label = stringResource(R.string.confirm_password),
                    isError = confirmPasswordError != null,
                    errorMessage = confirmPasswordError?.let { stringResource(it) } ?: "",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        if (state.confirmPassword.isNotBlank()){
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
            }


            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )

            ViewPrimaryButton(
                text = stringResource(R.string.btn_sign_up),
                onClick = {
                    focusManager.clearFocus()
                    onAction(SignUpAction.OnSignUpClicked)
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
                    text = stringResource(R.string.email_google_divider),
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
                onClick = onGoogleClicked,
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
    JetSpendingTheme {
        SignUpContent(
            state = SignUpState(),
            onAction = {},
            onNavigate = {},
            onGoogleClicked = {}
        )
    }
}