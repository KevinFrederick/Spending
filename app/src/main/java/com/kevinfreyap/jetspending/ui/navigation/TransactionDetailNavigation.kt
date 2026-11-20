package com.kevinfreyap.jetspending.ui.navigation

sealed interface TransactionDetailNavigation {
    data class NavigateToEditTransaction (val transactionId: String): TransactionDetailNavigation
    data object NavigateUp: TransactionDetailNavigation
}