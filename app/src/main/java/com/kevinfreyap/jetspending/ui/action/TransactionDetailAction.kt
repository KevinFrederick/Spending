package com.kevinfreyap.jetspending.ui.action

sealed interface TransactionDetailAction {
    data class OnToggleDeleteDialog(val isVisible: Boolean): TransactionDetailAction
    data object OnConfirmDelete: TransactionDetailAction
    data object OnSwapExchangeRate: TransactionDetailAction
}