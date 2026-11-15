package com.kevinfreyap.jetspending.ui.helper.state

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.SpendingIncomeStatus
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.helper.currency.toCurrencyString
import com.kevinfreyap.jetspending.ui.helper.date.toMonth
import com.kevinfreyap.jetspending.ui.state.MonthlySummaryState
import com.kevinfreyap.jetspending.ui.state.SummaryState
import java.time.Instant

fun SpendingIncomeStatus.toMonthlySummary(
    selectedCurrency: AppCurrency,
    instant: Instant
): MonthlySummaryState =
    MonthlySummaryState(
        incomeSummary = SummaryState(
            transactionType = TransactionType.INCOME,
            value = this.income.toCurrencyString(
                currency = selectedCurrency
            ),
            currency = selectedCurrency.name,
            month = instant.toMonth()
        ),
        spendingSummary = SummaryState(
            transactionType = TransactionType.SPENDING,
            value = this.spending.toCurrencyString(
                currency = selectedCurrency
            ),
            currency = selectedCurrency.name,
            month = instant.toMonth()
        )
    )