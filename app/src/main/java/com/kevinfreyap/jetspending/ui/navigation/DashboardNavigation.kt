package com.kevinfreyap.jetspending.ui.navigation

sealed interface DashboardNavigation {
    data class NavigateToTransactionDetail(val transactionId: String): DashboardNavigation
    data object NavigateToTransactionList: DashboardNavigation
    data object NavigateToAddTransaction: DashboardNavigation
}