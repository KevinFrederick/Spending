package com.kevinfreyap.jetspending.ui.navigation

sealed interface SignInNavigation {
    data object NavigateUp: SignInNavigation
    data object NavigateToSignUp: SignInNavigation
    data object NavigateToDashboard: SignInNavigation
}