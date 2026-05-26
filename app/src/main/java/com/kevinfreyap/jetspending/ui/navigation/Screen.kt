package com.kevinfreyap.jetspending.ui.navigation

sealed class Screen(val route: String) {
    data object OnBoarding: Screen("onboarding")
    data object SignUp: Screen("sign_up")
    data object SignIn: Screen("sign_in")
    data object Dashboard: Screen("dashboard")
    data object Report: Screen("report")
    data object Settings: Screen("settings")
    data object AddTransaction: Screen("add_transaction") {
        const val ROUTE_WITH_ARGS = "add_transaction?transactionId={transactionId}"
        fun createRoute(transactionId: String? = null): String {
            return if (transactionId != null) {
                "add_transaction?transactionId=$transactionId"
            } else {
                "add_transaction"
            }
        }
    }
    data object TransactionList: Screen("transactions")
    data object TransactionDetail: Screen("detail/{transactionId}") { // detail/{transactionId} -> using '/' transaction is required
        fun createRoute(transactionId: String) = "detail/$transactionId"
    }
    data object EditProfile: Screen("edit_profile")
    data object Notification: Screen("notification")
    data object PrivacySecurity: Screen("privacy_security")
}