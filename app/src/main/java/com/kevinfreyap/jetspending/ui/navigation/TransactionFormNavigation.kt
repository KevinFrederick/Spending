package com.kevinfreyap.jetspending.ui.navigation

sealed interface TransactionFormNavigation {
    data object NavigateUp: TransactionFormNavigation
}