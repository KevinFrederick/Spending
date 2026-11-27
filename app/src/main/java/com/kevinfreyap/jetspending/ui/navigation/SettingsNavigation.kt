package com.kevinfreyap.jetspending.ui.navigation

sealed interface SettingsNavigation {
    data object NavigateToEditProfile: SettingsNavigation
    data object NavigateToNotification: SettingsNavigation
    data object NavigateToSecurity: SettingsNavigation
    data object NavigateToOnBoard: SettingsNavigation
}