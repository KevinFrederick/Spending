package com.kevinfreyap.jetspending.ui.navigation

sealed interface TransactionListNavigation {
    data class NavigateToTransactionDetail (val transactionId: String): TransactionListNavigation
    data object NavigateToAddTransaction: TransactionListNavigation
}