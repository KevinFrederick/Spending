package com.kevinfreyap.jetspending.utils.formatter

import androidx.compose.ui.graphics.Color
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.Orange700
import java.math.BigDecimal

object TransactionItemUiFormatter {
    fun formatTransactionAmount(
        transactionAmount: BigDecimal,
        transactionType: TransactionType,
        selectedCurrency: AppCurrency
    ): String {
        val symbol = when(transactionType) {
            TransactionType.INCOME -> "+"
            TransactionType.SPENDING -> "-"
        }
        val amountWithCode = CurrencyUiFormatter.formatWithCode(transactionAmount.toPlainString(), selectedCurrency)
        return "$symbol $amountWithCode"
    }

    fun getBackgroundColor(transactionType: TransactionType): Color {
        return when (transactionType) {
            TransactionType.INCOME -> Green500
            TransactionType.SPENDING -> Orange700
        }
    }
}