package com.kevinfreyap.jetspending.ui.screen.detail.state

import androidx.annotation.DrawableRes
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType

data class TransactionDetailSummaryState(
    @DrawableRes
    val categoryIconRes: Int,
    val name: String,
    val type: TransactionType,
    val originalAmountWithCurrency: String,
    val originalCurrency: AppCurrency,
    val convertedAmountWithCurrency: String?,
    val convertedCurrency: AppCurrency
) {
    val isSameCurrency: Boolean
        get() = originalCurrency == convertedCurrency
}
