package com.kevinfreyap.jetspending.ui.state

import androidx.annotation.StringRes
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.screen.settings.state.ProfileCardState
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsDialogType

data class SettingsState(
    val profile: ProfileCardState = ProfileCardState(),
    val selectedCurrency: AppCurrency = AppCurrency.DEFAULT,
    val selectedAppearance: AppTheme = AppTheme.SYSTEM,
    val activeDialog: SettingsDialogType = SettingsDialogType.NONE,
    val uiState: UiState<Unit> = UiState.Idle
) {
    @get:StringRes
    val selectedAppearanceRes: Int
        get() = when(selectedAppearance) {
            AppTheme.LIGHT -> R.string.theme_light
            AppTheme.DARK -> R.string.theme_dark
            AppTheme.SYSTEM -> R.string.theme_system
        }
}
