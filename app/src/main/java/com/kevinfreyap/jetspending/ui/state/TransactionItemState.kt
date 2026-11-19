package com.kevinfreyap.jetspending.ui.state

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kevinfreyap.domain.model.TransactionType

data class TransactionItemState(
    val transactionId: String,
    val transactionType: TransactionType,
    @DrawableRes
    val transactionCategoryIcon: Int,
    val transactionName: String,
    @StringRes
    val transactionCategoryName: Int,
    val transactionDate: String,
    val transactionMonthYear: String,
    val transactionOriginalAmount: String,
    val transactionConvertedAmount: String?,
    val transactionSelectedCurrency: String,
    val transactionOriginalCurrency: String
)
