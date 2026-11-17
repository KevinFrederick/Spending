package com.kevinfreyap.jetspending.ui.action

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.model.CategoryUI

sealed interface TransactionFormAction {
    sealed interface TransactionOverlayAction: TransactionFormAction {
        data class OnToggleCategoryBottomSheet(val isVisible: Boolean): TransactionOverlayAction
        data class OnToggleDateDialog(val isVisible: Boolean): TransactionOverlayAction
        data class OnToggleDiscardDialog(val isVisible: Boolean): TransactionOverlayAction
        data object OnConfirmDiscard: TransactionOverlayAction
    }

    sealed interface TransactionMainAction: TransactionFormAction {
        data class OnSelectTransactionType(val type: TransactionType): TransactionMainAction
        data class OnAmountChange(val amount: String): TransactionMainAction
        data class OnCurrencySelected(val currency: AppCurrency): TransactionMainAction
        data class OnCategorySelected(val category: CategoryUI): TransactionMainAction
        data class OnNameChange(val name: String): TransactionMainAction
        data class OnNotesChange(val notes: String): TransactionMainAction
        data class OnDateSelected(val date: Long): TransactionMainAction
    }

    data object SubmitTransaction: TransactionFormAction
}