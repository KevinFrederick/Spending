package com.kevinfreyap.jetspending.ui.screen.edit_profile

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.v2.ViewAppDialog
import com.kevinfreyap.jetspending.ui.components.v2.ViewPrimaryButton
import com.kevinfreyap.jetspending.ui.components.v2.ViewTextInputField
import com.kevinfreyap.jetspending.ui.components.v2.ViewTopBar
import com.kevinfreyap.jetspending.ui.components.v2.ViewUserProfile
import com.kevinfreyap.jetspending.ui.model.DialogType
import com.kevinfreyap.jetspending.ui.action.EditProfileAction
import com.kevinfreyap.jetspending.ui.navigation.EditProfileNavigation
import com.kevinfreyap.jetspending.ui.state.EditProfileState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.UiEvent

@Composable
fun EditProfileScreen(
    onNavigate: (EditProfileNavigation) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val state by viewModel.state.collectAsState()

    val handleBackAttempt = {
        if (state.hasChanges) {
            viewModel.onAction(
                EditProfileAction.OnShowDiscardDialog(
                    isVisible = true
                )
            )
        } else {
            onNavigate(EditProfileNavigation.NavigateUp)
        }
    }

    BackHandler(enabled = state.hasChanges) {
        viewModel.onAction(
            EditProfileAction.OnShowDiscardDialog(
                isVisible = true
            )
        )
    }

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

    EditProfileContent(
        state = state,
        onBackAttempt = handleBackAttempt,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
fun EditProfileContent(
    state: EditProfileState,
    onBackAttempt: () -> Unit,
    onAction: (EditProfileAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val usernameError = if (state.uiState is UiState.ValidationErrors) {
        state.uiState.errors[Field.USERNAME]
    } else null

    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.edit_profile),
                onBackClick = onBackAttempt,
                isLoading = state.uiState is UiState.Loading
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    top = 12.dp,
                    bottom = 24.dp,
                    start = 24.dp,
                    end = 24.dp
                )
                .fillMaxSize()
        ) {
            ViewUserProfile(
                imageUrl = state.imageUrl,
                name = state.userName,
                size = 88.dp,
                textStyle = MaterialTheme.typography.headlineLarge
            )

            Spacer(
                modifier = Modifier
                    .height(32.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ViewTextInputField(
                    value = state.userName,
                    onValueChange = {
                        onAction(
                            EditProfileAction.OnUsernameChange(
                                name = it
                            )
                        )
                    },
                    label = stringResource(R.string.username),
                    isError = usernameError != null,
                    errorMessage = usernameError?.let { stringResource(it) } ?: "",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    )
                )

                ViewTextInputField(
                    value = state.email,
                    onValueChange = {},
                    label = stringResource(R.string.email),
                    enabled = false,
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_mail_24),
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                )
            }

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .align(Alignment.Start)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info_24),
                    contentDescription = null,
                    tint = Theme.custom.secondaryTextColor.copy(0.6f),
                    modifier = Modifier
                        .size(12.dp)
                )

                Text(
                    text = stringResource(R.string.hint_profile_email),
                    style = MaterialTheme.typography.labelSmall,
                    color = Theme.custom.secondaryTextColor.copy(0.6f)
                )
            }

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            ViewPrimaryButton(
                text = stringResource(R.string.btn_save_changes),
                enabled = state.hasChanges && state.userName.isNotBlank() && state.uiState !is UiState.Loading,
                onClick = {
                    onAction(
                        EditProfileAction.OnSaveChanges
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_check_24),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }

    if (state.isDiscardDialogVisible) {
        ViewAppDialog(
            dialogType = DialogType.WARNING,
            title = stringResource(R.string.dialog_discard_changes),
            message = stringResource(R.string.description_discard_change_user),
            confirmText = stringResource(R.string.discard),
            onConfirm = {
                onAction(
                    EditProfileAction.OnConfirmDiscard
                )

                onAction(
                    EditProfileAction.OnShowDiscardDialog(
                        isVisible = false
                    )
                )
            },
            dismissText = stringResource(R.string.btn_keep_editing),
            onDismissRequest = {
                onAction(
                    EditProfileAction.OnShowDiscardDialog(
                        isVisible = false
                    )
                )
            },
        )
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
            state = EditProfileState(
                userName = "John Doe",
                email = "JohnDoe@email.com"
            ),
            onAction = {},
            onBackAttempt = {}
        )
    }
}
