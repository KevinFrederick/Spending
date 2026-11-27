package com.kevinfreyap.jetspending.ui.helper.state

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionConverted
import com.kevinfreyap.jetspending.ui.helper.category.toCategoryUI
import com.kevinfreyap.jetspending.ui.helper.currency.toCurrencyString
import com.kevinfreyap.jetspending.ui.helper.date.toFullDateShortMonth
import com.kevinfreyap.jetspending.ui.helper.date.toFullMonthYear
import com.kevinfreyap.jetspending.ui.state.TransactionItemState

fun TransactionConverted.toTransactionItemState(currency: AppCurrency): TransactionItemState {
    val categoryUI = this.transaction.category.toCategoryUI()

    return TransactionItemState(
        transactionId = this.transaction.id,
        transactionType = this.transaction.type,
        transactionCategoryIcon = categoryUI.iconRes,
        transactionName = this.transaction.name,
        transactionCategoryName = categoryUI.name,
        transactionDate = this.transaction.date.toFullDateShortMonth(),
        transactionMonthYear = this.transaction.date.toFullMonthYear(),
        transactionOriginalAmount = this.transaction.amount.toCurrencyString(transaction.currency),
        transactionConvertedAmount = this.convertedAmount?.toCurrencyString(currency),
        transactionSelectedCurrency = currency.name,
        transactionOriginalCurrency = this.transaction.currency.name
    )
}