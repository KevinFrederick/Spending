package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.screen.detail.state.TransactionDetailSummaryState

data class TransactionDetailState(
    val summary: TransactionDetailSummaryState,
    val category: CategoryUI,
    val dateText: String,
    val originalCurrency: AppCurrency,
    val convertedCurrency: AppCurrency?,
    val exchangeRate: String?,
    val notes: String?,
) {
    val isCurrencyDifferent: Boolean
        get() = originalCurrency != convertedCurrency
}
