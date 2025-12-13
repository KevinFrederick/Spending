package com.kevinfreyap.jetspending.utils.mapper

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.jetspending.ui.model.TransactionItemUi
import com.kevinfreyap.jetspending.utils.formatter.CategoryUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import com.kevinfreyap.jetspending.utils.formatter.TransactionItemUiFormatter
import javax.inject.Inject

class TransactionItemUiMapper @Inject constructor() {
    fun mapTransactionDomainToUi(transaction: Transaction): TransactionItemUi {
        return TransactionItemUi(
            transactionId = transaction.id,
            transactionName = transaction.name,
            transactionTypeBackground = TransactionItemUiFormatter.getBackgroundColor(transaction.type),
            transactionCategoryIcon = CategoryUiFormatter.mapCategoryDomainToUi(transaction.category).iconRes,
            transactionAmount = TransactionItemUiFormatter.formatTransactionAmount(
                transaction.amount,
                transaction.type,
                AppCurrency.IDR
            ),
            transactionDate = DateFormatter.formatToDate(transaction.date),
            transactionDateRaw = transaction.date
        )
    }
}