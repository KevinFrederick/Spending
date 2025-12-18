package com.kevinfreyap.jetspending.utils.formatter

import androidx.compose.ui.graphics.Color
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.theme.Green500
import com.kevinfreyap.jetspending.ui.theme.Orange700
import java.math.BigDecimal
import java.math.RoundingMode

object TransactionUiFormatter {
    fun formatAmountType(
        transactionAmount: BigDecimal,
        transactionType: TransactionType,
        selectedCurrency: AppCurrency
    ): String {
        val symbol = when(transactionType) {
            TransactionType.INCOME -> "+"
            TransactionType.SPENDING -> "-"
        }

        val amountWithCode = formatAmount(transactionAmount, selectedCurrency)
        return "$symbol $amountWithCode"
    }

    fun formatAmount(
        transactionAmount: BigDecimal,
        selectedCurrency: AppCurrency
    ): String {
        val fraction = if (selectedCurrency.isFraction) 2 else 0

        return CurrencyUiFormatter.formatWithCode(
            transactionAmount.setScale(fraction, RoundingMode.HALF_UP).toPlainString(),
            selectedCurrency
        )
    }

    fun getBackgroundColor(transactionType: TransactionType): Color {
        return when (transactionType) {
            TransactionType.INCOME -> Green500
            TransactionType.SPENDING -> Orange700
        }
    }
}