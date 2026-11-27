package com.kevinfreyap.jetspending.ui.navigation

sealed interface SignUpNavigation {
    data object NavigateUp: SignUpNavigation
    data object NavigateToSignIn: SignUpNavigation
    data object NavigateToDashboard: SignUpNavigation
}