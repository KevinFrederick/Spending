package com.kevinfreyap.jetspending.ui.navigation

sealed interface OnboardingNavigation {
    data object NavigateToSignIn: OnboardingNavigation
    data object NavigateToSignUp: OnboardingNavigation
}