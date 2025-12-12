package com.kevinfreyap.jetspending.ui.screen.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ProfileCard
import com.kevinfreyap.jetspending.ui.components.ViewCustomDialog
import com.kevinfreyap.jetspending.ui.components.ViewSettingsGroup
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.model.SettingsGroup
import com.kevinfreyap.jetspending.ui.model.SettingsOption
import com.kevinfreyap.jetspending.ui.model.UserProfileUi
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Orange700
import com.kevinfreyap.jetspending.ui.theme.Red500
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    navigateToOnBoarding: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsGroup by viewModel.settingsState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigationChannel.collectLatest {
            navigateToOnBoarding()
        }
    }

    SettingsContent(
        user = userProfile,
        settingsGroup = settingsGroup,
        onSettingsClicked = { settingsOption ->
            if (settingsOption == SettingsOption.LOG_OUT) {
                showLogoutDialog = true
            }
        },
        showLogoutDialog = showLogoutDialog,
        onLogoutDialogPositiveBtn = {
            showLogoutDialog = false
            viewModel.logout()
        },
        onLogoutDialogNegativeBtn = {
            showLogoutDialog = false
        },
        modifier = modifier
    )
}

@Composable
fun SettingsContent(
    user: UserProfileUi,
    settingsGroup: List<SettingsGroup>,
    onSettingsClicked: (SettingsOption) -> Unit,
    showLogoutDialog: Boolean,
    onLogoutDialogPositiveBtn: () -> Unit,
    onLogoutDialogNegativeBtn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.settings)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    top = 8.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ProfileCard(
                imageUrl = user.imageUrl,
                name = user.displayName,
                email = user.displayEmail,
            )

            settingsGroup.forEach { group ->
                ViewSettingsGroup(
                    groupTitle = group.groupTitle,
                    onSettingsClicked = onSettingsClicked,
                    settings = group.settings
                )
            }
        }

        if (showLogoutDialog) {
            ViewCustomDialog(
                onDismissRequest = onLogoutDialogNegativeBtn,
                icon = R.drawable.ic_error_outline_24,
                iconColor = Orange700,
                title = stringResource(R.string.logout),
                message = stringResource(R.string.description_log_out),
                positiveBtn = {
                    Button(
                        onClick = onLogoutDialogPositiveBtn,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Red500
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.confirm),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                negativeBtn = {
                    Button(
                        onClick = onLogoutDialogNegativeBtn,
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
fun SettingsContentPreview() {
    val viewModel: SettingsViewModel = hiltViewModel()
    val settingsGroup by viewModel.settingsState.collectAsState()

    JetSpendingTheme {
        SettingsContent(
            user = UserProfileUi(
                imageUrl = "",
                displayName = "John Doe",
                displayEmail = "JohnDow@email.com"
            ),
            settingsGroup = settingsGroup,
            onSettingsClicked = {},
            showLogoutDialog = false,
            onLogoutDialogPositiveBtn = {},
            onLogoutDialogNegativeBtn = {},
        )
    }
}