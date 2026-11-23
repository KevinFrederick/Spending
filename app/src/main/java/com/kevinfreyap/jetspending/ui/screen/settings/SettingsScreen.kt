package com.kevinfreyap.jetspending.ui.screen.settings

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.jetspending.BuildConfig
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.SettingsAction
import com.kevinfreyap.jetspending.ui.components.ViewSelectionDialog
import com.kevinfreyap.jetspending.ui.components.ViewSelectionItem
import com.kevinfreyap.jetspending.ui.components.v2.ViewPrimaryButton
import com.kevinfreyap.jetspending.ui.components.v2.ViewTopBar
import com.kevinfreyap.jetspending.ui.navigation.SettingsNavigation
import com.kevinfreyap.jetspending.ui.screen.settings.components.ProfileCard
import com.kevinfreyap.jetspending.ui.screen.settings.components.ProfileCardPlaceholder
import com.kevinfreyap.jetspending.ui.screen.settings.components.SettingsSection
import com.kevinfreyap.jetspending.ui.screen.settings.state.ProfileCardState
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsDialogType
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsItemState
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsItemType
import com.kevinfreyap.jetspending.ui.state.SettingsState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.theme.JetSpendingTheme
import com.kevinfreyap.jetspending.ui.theme.Theme
import com.kevinfreyap.jetspending.utils.UiEvent

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigate: (SettingsNavigation) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

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

    SettingsContent(
        state = state,
        onAction = viewModel::onAction,
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@Composable
fun SettingsContent(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigate: (SettingsNavigation) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            ViewTopBar(
                title = stringResource(R.string.settings),
            )
        },
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(28.dp),
            modifier = modifier
                .padding(innerPadding)
                .padding(
                    top = 8.dp,
                    bottom = 24.dp,
                    start = 24.dp,
                    end = 24.dp
                )
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (state.uiState is UiState.Loading) {
                ProfileCardPlaceholder()
            } else {
                ProfileCard(
                    state = state.profile,
                    onClick = {
                        onNavigate(
                            SettingsNavigation.NavigateToEditProfile
                        )
                    }
                )
            }

            SettingsSection(
                header = R.string.settings_header_preferences,
                settings = listOf(
                    SettingsItemState(
                        type = SettingsItemType.BASE_CURRENCY,
                        iconRes = R.drawable.ic_currency_exchange_24,
                        name = R.string.settings_base_currency,
                        description = R.string.description_settings_base_currency,
                        selectedItem = state.selectedCurrency.name,
                    ),
                    SettingsItemState(
                        type = SettingsItemType.APPEARANCE,
                        iconRes = R.drawable.ic_light_mode_24,
                        name = R.string.settings_appearance,
                        description = R.string.description_settings_appearance,
                        selectedItem = stringResource(state.selectedAppearanceRes)
                    ),
                ),
                onItemClick = {settingsItem ->
                    when(settingsItem) {
                        SettingsItemType.BASE_CURRENCY -> onAction(
                            SettingsAction.OnShowDialog(SettingsDialogType.CURRENCY)
                        )
                        SettingsItemType.APPEARANCE -> onAction(
                            SettingsAction.OnShowDialog(SettingsDialogType.APPEARANCE)
                        )
                        else -> Unit
                    }
                },
                isLoading = state.uiState is UiState.Loading
            )

            SettingsSection(
                header = R.string.settings_header_app_settings,
                settings = listOf(
                    SettingsItemState(
                        type = SettingsItemType.NOTIFICATION,
                        iconRes = R.drawable.ic_notifications_24,
                        name = R.string.settings_notification,
                        description = R.string.description_settings_notification,
                    ),
                    SettingsItemState(
                        type = SettingsItemType.SECURITY,
                        iconRes = R.drawable.ic_shield_check,
                        name = R.string.settings_security,
                        description = R.string.description_settings_security,
                    ),
                ),
                onItemClick = {settingsItem ->
                    when(settingsItem) {
                        SettingsItemType.NOTIFICATION -> {
                            onNavigate(
                                SettingsNavigation.NavigateToNotification
                            )
                        }
                        SettingsItemType.SECURITY -> {
                            onNavigate(
                                SettingsNavigation.NavigateToSecurity
                            )
                        }
                        else -> Unit
                    }
                }
            )

            Spacer(Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ViewPrimaryButton(
                    text = stringResource(R.string.btn_sign_out),
                    containerColor = Theme.custom.negativeContainer,
                    contentColor = Theme.custom.statusNegative,
                    onClick = {
                        onAction(
                            SettingsAction.OnLogout
                        )
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout_24),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )

                Text(
                    text = "${stringResource(R.string.app_name)} • ${stringResource(R.string.label_version, BuildConfig.VERSION_NAME)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.custom.secondaryTextColor.copy(0.6f)
                )
            }
        }
    }

    when (state.activeDialog) {
        SettingsDialogType.NONE -> Unit
        SettingsDialogType.CURRENCY -> {
            ViewSelectionDialog(
                titleRes = R.string.dialog_title_select_currency,
                options = {
                    AppCurrency.entries.forEach { currency ->
                        ViewSelectionItem(
                            title = currency.name,
                            subtitle = currency.displayName,
                            isSelected = currency == state.selectedCurrency,
                            onClick = {
                                onAction(
                                    SettingsAction.OnSelectCurrency(
                                        currency = currency
                                    )
                                )

                                onAction(
                                    SettingsAction.OnShowDialog(
                                        SettingsDialogType.NONE
                                    )
                                )
                            }
                        )
                    }
                },
                onDismissRequest = {
                    onAction(
                        SettingsAction.OnShowDialog(
                            SettingsDialogType.NONE
                        )
                    )
                },
                subtitle = stringResource(R.string.description_settings_base_currency)
            )
        }
        SettingsDialogType.APPEARANCE -> {
            ViewSelectionDialog(
                titleRes = R.string.dialog_title_select_theme,
                options = {
                    AppTheme.entries.forEach { theme ->
                        ViewSelectionItem(
                            title = stringResource(
                                when(theme) {
                                    AppTheme.LIGHT -> R.string.theme_light_mode
                                    AppTheme.DARK -> R.string.theme_dark_mode
                                    AppTheme.SYSTEM -> R.string.theme_follow_system
                                }
                            ),
                            isSelected = theme == state.selectedAppearance,
                            onClick = {
                                onAction(
                                    SettingsAction.OnSelectTheme(
                                        theme = theme
                                    )
                                )

                                onAction(
                                    SettingsAction.OnShowDialog(
                                        SettingsDialogType.NONE
                                    )
                                )
                            }
                        )
                    }
                },
                onDismissRequest = {
                    onAction(
                        SettingsAction.OnShowDialog(
                            SettingsDialogType.NONE
                        )
                    )
                },
                subtitle = stringResource(R.string.description_settings_appearance)
            )
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    device = Devices.PIXEL_9_PRO,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SettingsContentPreview() {
    JetSpendingTheme {
        SettingsContent(
            state = SettingsState(
                profile = ProfileCardState(
                    imageUrl = null,
                    name = "John Doe",
                    email = "JohnDoe@email.com",
                ),
                selectedCurrency = AppCurrency.IDR,
            ),
            onAction = {},
            onNavigate = {}
        )
    }
}