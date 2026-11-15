package com.kevinfreyap.domain.helper

import com.kevinfreyap.domain.model.AggregatedTotals
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionMathWithRates
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.usecase.currency.CalculateAmountBasedOnRatesUseCase
import java.math.BigDecimal

fun List<TransactionMathWithRates>.aggregateTotals(
    targetCurrency: AppCurrency,
    calculateAmount: CalculateAmountBasedOnRatesUseCase
): AggregatedTotals {
    if (isEmpty()) {
        return AggregatedTotals(
            income = BigDecimal.ZERO,
            spending = BigDecimal.ZERO,
            isIncomplete = false
        )
    }

    var totalIncome = BigDecimal.ZERO
    var totalSpending = BigDecimal.ZERO
    var isIncomplete = false

    // Group transaction with same currency, type and date
    val groupedTransactions = groupingBy {
        Triple(it.rates, it.currency, it.type)
    }
        // Each group start with 0, add each transaction.amount
        .fold(BigDecimal.ZERO) { acc, transaction ->
            acc.add(transaction.amount)
        }

    // Convert each group
    groupedTransactions.forEach { (key, groupAmount) ->
        val (rates, sourceCurrency, type) = key
        val convertedAmount = calculateAmount(
            amount = groupAmount,
            sourceCurrency = sourceCurrency,
            targetCurrency = targetCurrency,
            rates = rates
        )

        if (convertedAmount != null) {
            if (type == TransactionType.INCOME) {
                totalIncome += convertedAmount
            } else {
                totalSpending += convertedAmount
            }
        } else {
            isIncomplete = true
        }
    }

    return AggregatedTotals(
        income = totalIncome,
        spending = totalSpending,
        isIncomplete = isIncomplete
    )
}