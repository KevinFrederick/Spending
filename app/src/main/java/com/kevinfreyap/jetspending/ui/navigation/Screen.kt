package com.kevinfreyap.jetspending.ui.navigation

sealed class Screen(val route: String) {
    data object OnBoarding: Screen("onboarding")
    data object SignUp: Screen("sign_up")
    data object SignIn: Screen("sign_in")
    data object Dashboard: Screen("dashboard")
    data object Report: Screen("report")
    data object Settings: Screen("settings")
    data object AddTransaction: Screen("add_transaction")
}