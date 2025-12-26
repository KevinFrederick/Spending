package com.kevinfreyap.jetspending.ui.screen.settings

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.components.ProfileCard
import com.kevinfreyap.jetspending.ui.components.ViewCustomDialog
import com.kevinfreyap.jetspending.ui.components.ViewSelectionDialog
import com.kevinfreyap.jetspending.ui.components.ViewSelectionItem
import com.kevinfreyap.jetspending.ui.components.ViewSettingsGroup
import com.kevinfreyap.jetspending.ui.components.ViewTopBar
import com.kevinfreyap.jetspending.ui.main.MainViewModel
import com.kevinfreyap.jetspending.ui.model.SettingsGroup
import com.kevinfreyap.jetspending.ui.model.SettingsItem
import com.kevinfreyap.jetspending.ui.model.SettingsOption
import com.kevinfreyap.jetspending.ui.model.UserProfileUi
import com.kevinfreyap.jetspending.ui.theme.Grey500
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Orange700
import com.kevinfreyap.jetspending.ui.theme.Red500
import com.kevinfreyap.jetspending.ui.theme.Theme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    navigateToOnBoarding: () -> Unit,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = hiltViewModel(),
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val currencyCode by mainViewModel.selectedCurrency.collectAsState()

    val selectedTheme by viewModel.selectedTheme.collectAsState()
    val settingsGroup by viewModel.settingsState.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigationChannel.collectLatest {
            navigateToOnBoarding()
        }
    }

    SettingsContent(
        currencyCode = currencyCode,
        selectedTheme = selectedTheme,
        user = userProfile,
        settingsGroup = settingsGroup,
        onSettingsClicked = { settingsOption ->
            when(settingsOption) {
                SettingsOption.EDIT_PROFILE -> {

                }
                SettingsOption.NOTIFICATION -> {

                }
                SettingsOption.PRIVACY_SECURITY -> {

                }
                SettingsOption.CURRENCY -> {
                    showCurrencyDialog = true
                }
                SettingsOption.THEME -> {
                    showThemeDialog = true
                }
                SettingsOption.LOG_OUT -> {
                    showLogoutDialog = true
                }
            }
        },
        showCurrencyDialog = showCurrencyDialog,
        showThemeDialog = showThemeDialog,
        showLogoutDialog = showLogoutDialog,
        onSelectCurrency = {
            mainViewModel.onSelectCurrency(it)
            showCurrencyDialog = false
        },
        onSelectTheme = {
            viewModel.onThemeSelected(it)
            showThemeDialog = false
        },
        onDismissCurrencyDialog = {
            showCurrencyDialog = false
        },
        onDismissThemeDialog = {
            showThemeDialog = false
        },
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
    currencyCode: AppCurrency,
    selectedTheme: AppTheme,
    user: UserProfileUi,
    settingsGroup: List<SettingsGroup>,
    showCurrencyDialog: Boolean,
    showThemeDialog: Boolean,
    showLogoutDialog: Boolean,
    onSettingsClicked: (SettingsOption) -> Unit,
    onSelectCurrency: (AppCurrency) -> Unit,
    onSelectTheme: (AppTheme) -> Unit,
    onDismissCurrencyDialog: () -> Unit,
    onDismissThemeDialog: () -> Unit,
    onLogoutDialogPositiveBtn: () -> Unit,
    onLogoutDialogNegativeBtn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.settings),
                onSelectCurrency = {}
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

        if (showCurrencyDialog) {
            ViewSelectionDialog(
                title = stringResource(R.string.select_currency),
                subtitle = stringResource(R.string.description_select_currency),
                onDismissRequest = onDismissCurrencyDialog,
                options = {
                    AppCurrency.entries.forEachIndexed { index, currency ->
                        ViewSelectionItem(
                            title = currency.countryName,
                            subtitle = currency.displayCurrencyName,
                            selected = currency == currencyCode,
                            modifier = Modifier
                                .clickable(
                                    onClick = { onSelectCurrency(currency) }
                                )
                        )

                        if (index < AppCurrency.entries.lastIndex) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = Theme.custom.hintColor,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            )
        }

        if (showThemeDialog) {
            ViewSelectionDialog(
                title = stringResource(R.string.select_theme),
                subtitle = stringResource(R.string.select_theme_subtitle),
                onDismissRequest = onDismissThemeDialog,
                options = {
                    AppTheme.entries.forEachIndexed { index, theme ->
                        val displayName = when(theme) {
                            AppTheme.LIGHT -> R.string.light_mode
                            AppTheme.DARK -> R.string.dark_mode
                            AppTheme.SYSTEM -> R.string.follow_system
                        }

                        ViewSelectionItem(
                            title = stringResource(displayName),
                            selected = selectedTheme == theme,
                            modifier = Modifier
                                .clickable(
                                    onClick = { onSelectTheme(theme) }
                                )
                        )
                    }
                }
            )
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
    val generalList = listOf(
        SettingsItem(
            id = SettingsOption.EDIT_PROFILE,
            title = R.string.edit_profile,
            icon = R.drawable.ic_mode_edit_24,
        ),
        SettingsItem(
            id = SettingsOption.NOTIFICATION,
            title = R.string.notifications,
            icon = R.drawable.ic_notifications_24,
        ),
        SettingsItem(
            id = SettingsOption.PRIVACY_SECURITY,
            title = R.string.security,
            icon = R.drawable.ic_lock_24,
        ),
    )

    val preferencesList = listOf(
        SettingsItem(
            id = SettingsOption.CURRENCY,
            title = R.string.currency,
            icon = R.drawable.ic_currency_exchange_24,
            subtitle = "Indonesia (Rupiah | Rp)"
        ),
        SettingsItem(
            id = SettingsOption.THEME,
            title = R.string.theme,
            icon = R.drawable.ic_light_mode_24,
            subtitle = "Dark Mode"
        ),
    )

    val supportList = listOf(
        SettingsItem(
            id = SettingsOption.LOG_OUT,
            title = R.string.logout,
            icon = R.drawable.ic_logout_24,
            contentColor = Red500,
            showChevron = false
        ),
    )

    val settingsGroup = listOf(
        SettingsGroup(R.string.general, generalList),
        SettingsGroup(R.string.preference, preferencesList),
        SettingsGroup(R.string.support, supportList),
    )

    JetSpendingTheme {
        SettingsContent(
            user = UserProfileUi(
                imageUrl = "",
                displayName = "John Doe",
                displayEmail = "JohnDow@email.com"
            ),
            settingsGroup = settingsGroup,
            onSettingsClicked = {},
            showCurrencyDialog = false,
            showLogoutDialog = false,
            showThemeDialog = true,
            onLogoutDialogPositiveBtn = {},
            onLogoutDialogNegativeBtn = {},
            currencyCode = AppCurrency.IDR,
            onDismissCurrencyDialog = {},
            onSelectCurrency = {},
            selectedTheme = AppTheme.SYSTEM,
            onSelectTheme = {},
            onDismissThemeDialog = {},
        )
    }
}