package com.kevinfreyap.jetspending.ui.navigation

sealed class Screen(val route: String) {
    data object Dashboard: Screen("dashboard")
    data object Report: Screen("report")
    data object AddTransaction: Screen("add_transaction")
}