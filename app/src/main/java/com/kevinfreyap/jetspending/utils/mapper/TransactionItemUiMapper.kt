package com.kevinfreyap.jetspending.utils.mapper

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.model.TransactionWithRates
import com.kevinfreyap.domain.usecase.currency.CurrencyUseCase
import com.kevinfreyap.jetspending.ui.model.TransactionItemUi
import com.kevinfreyap.jetspending.utils.formatter.CategoryUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.CurrencyUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import java.math.BigDecimal
import javax.inject.Inject

class TransactionItemUiMapper @Inject constructor(
    private val currencyUseCase: CurrencyUseCase
) {
    fun mapTransactionDomainToUi(
        populatedTransaction: TransactionWithRates,
        selectedCurrency: AppCurrency
    ): TransactionItemUi {
        val transaction = populatedTransaction.transaction
        val rates = populatedTransaction.rates

        val calculateRatesValue = currencyUseCase.calculateAmountBasedOnRates(
            amount = transaction.amount,
            sourceCurrency = transaction.currency,
            targetCurrency = selectedCurrency,
            rates = rates
        )

        return TransactionItemUi(
            transactionId = transaction.id,
            transactionName = transaction.name,
            transactionTypeBackground = CategoryUiFormatter.getBackgroundColor(transaction.type),
            transactionCategoryIcon = CategoryUiFormatter.mapCategoryDomainToUi(transaction.category).iconRes,
            transactionAmount =
                formatAmountType(
                    calculateRatesValue ?: transaction.amount,
                    transaction.type,
                    if (calculateRatesValue != null) selectedCurrency else transaction.currency
                ),
            transactionDate = DateFormatter.formatToDate(transaction.date),
            transactionDateRaw = transaction.date,
            isConversionPending = calculateRatesValue == null
        )
    }

    fun formatAmountType(
        transactionAmount: BigDecimal,
        transactionType: TransactionType,
        selectedCurrency: AppCurrency
    ): String {
        val symbol = when(transactionType) {
            TransactionType.INCOME -> "+"
            TransactionType.SPENDING -> "-"
        }

        val amountWithCode = CurrencyUiFormatter.formatWithCode(transactionAmount, selectedCurrency)
        return "$symbol $amountWithCode"
    }
}