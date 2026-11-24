package com.kevinfreyap.jetspending.ui.navigation

sealed interface PrivacySecurityNavigation {
    data object NavigateUp: PrivacySecurityNavigation
    data object NavigateToOnboard: PrivacySecurityNavigation
}