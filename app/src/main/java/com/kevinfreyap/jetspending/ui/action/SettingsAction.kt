package com.kevinfreyap.jetspending.ui.action

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsDialogType

sealed interface SettingsAction {
    data class OnSelectCurrency(val currency: AppCurrency): SettingsAction
    data class OnSelectTheme(val theme: AppTheme): SettingsAction
    data class OnShowDialog(val dialog: SettingsDialogType): SettingsAction
    data object OnLogout: SettingsAction
}