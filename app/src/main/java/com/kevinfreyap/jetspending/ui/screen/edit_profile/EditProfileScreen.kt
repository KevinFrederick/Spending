package com.kevinfreyap.jetspending.ui.screen.edit_profile

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ViewCustomDialog
import com.kevinfreyap.jetspending.ui.components.ViewTextField
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.ViewUserProfile
import com.kevinfreyap.jetspending.ui.state.EditProfileAction
import com.kevinfreyap.jetspending.ui.state.EditProfileState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Orange700
import com.kevinfreyap.jetspending.ui.theme.Red500
import com.kevinfreyap.jetspending.ui.theme.Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val showSuccessDialog by viewModel.showSuccessDialog.collectAsState()

    val (showConfirmationDialog, setShowConfirmationDialog) = remember { mutableStateOf(false) }

    val editAction = remember(viewModel) {
        object : EditProfileAction {
            override fun onUserNameChange(newUserName: String) {
                viewModel.onUsernameChange(newUserName)
            }

            override fun onImageSelected(newImageUrl: String) {
                viewModel.onImageSelected(newImageUrl)
            }

            override fun dismissSuccessDialog() {
                viewModel.onDismissSuccessDialog()
                onBackClick()
            }

            override fun saveProfile() {
                viewModel.saveProfile()
            }

        }
    }

    EditProfileContent(
        onBackClick = onBackClick,
        onShowConfirmationDialog = {
            setShowConfirmationDialog(it)
        },
        uiState = uiState,
        state = state,
        action = editAction,
        showSuccessDialog = showSuccessDialog,
        showConfirmationDialog = showConfirmationDialog,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    onBackClick: () -> Unit,
    onShowConfirmationDialog: (Boolean) -> Unit,
    uiState: UiState<Unit>,
    state: EditProfileState,
    action: EditProfileAction,
    showSuccessDialog: Boolean,
    showConfirmationDialog: Boolean,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val usernameError = if (uiState is UiState.ValidationErrors) {
        uiState.errors[Field.USERNAME]
    } else null

    BackHandler(
        enabled = state.hasChanges
    ) {
        onShowConfirmationDialog(true)
    }

    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.edit_profile),
                onBackClick = {
                    if (!state.hasChanges) {
                        onBackClick()
                    } else {
                        onShowConfirmationDialog(true)
                    }
                },
                onSelectCurrency = {},
                isLoading = uiState is UiState.Loading
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    top = 32.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxSize()
        ) {
            ViewUserProfile(
                imageUrl = state.imageUrl,
                name = state.userName,
                size = 160.dp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            TextButton(
                onClick = {}
            ) {
                Text(
                    text = stringResource(R.string.change_picture),
                    textDecoration = TextDecoration.Underline,
                    color = Theme.custom.textColor,
                    fontSize = 16.sp
                )
            }

            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )

            ViewTextField(
                value = state.userName,
                onValueChange = action::onUserNameChange,
                label = stringResource(R.string.username),
                isError = usernameError != null,
                errorMessage = usernameError?.let { stringResource(it) } ?: "",
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )

            ViewTextField(
                value = state.email,
                onValueChange = {},
                label = stringResource(R.string.email),
                isEnabled = false
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            Button(
                onClick = action::saveProfile,
                enabled = state.hasChanges && uiState !is UiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = stringResource(R.string.save_changes),
                    fontSize = 18.sp
                )
            }
        }

        if (showSuccessDialog) {
            LaunchedEffect(Unit) {
                delay(1500)
                action.dismissSuccessDialog()
            }

            ViewCustomDialog(
                onDismissRequest = action::dismissSuccessDialog,
                icon = R.drawable.ic_check_circle_outline_24,
                iconColor = Green500,
                title = stringResource(R.string.success),
                message = stringResource(R.string.success_profile_update)
            )
        }

        if (showConfirmationDialog) {
            ViewCustomDialog(
                onDismissRequest = {
                    onShowConfirmationDialog(false)
                },
                icon = R.drawable.ic_error_outline_24,
                iconColor = Orange700,
                title = stringResource(R.string.discard_changes),
                message = stringResource(R.string.description_discard_change),
                positiveBtn = {
                    Button(
                        onClick = {
                            onShowConfirmationDialog(false)
                            scope.launch {
                                delay(200)
                                onBackClick()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Red500
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.discard),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                negativeBtn = {
                    Button(
                        onClick = {
                            onShowConfirmationDialog(false)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Grey500
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun EditProfilePreview() {
    JetSpendingTheme {
        EditProfileContent(
            onBackClick = {},
            onShowConfirmationDialog = {},
            state = EditProfileState(
                userName = "John Doe",
                email = "JohnDoe@gmail.com"
            ),
            action = object : EditProfileAction {
                override fun onUserNameChange(newUserName: String) {

                }

                override fun onImageSelected(newImageUrl: String) {

                }

                override fun dismissSuccessDialog() {

                }

                override fun saveProfile() {

                }

            },
            showSuccessDialog = false,
            showConfirmationDialog = false,
            uiState = UiState.Loading
        )
    }
}
