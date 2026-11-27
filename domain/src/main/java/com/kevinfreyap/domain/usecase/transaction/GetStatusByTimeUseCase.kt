package com.kevinfreyap.domain.usecase.transaction

import com.kevinfreyap.domain.helper.aggregateTotals
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.SpendingIncomeStatus
import com.kevinfreyap.domain.repository.ITransactionRepository
import com.kevinfreyap.domain.usecase.currency.CalculateAmountBasedOnRatesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class GetStatusByTimeUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository,
    private val calculateAmount: CalculateAmountBasedOnRatesUseCase
) {
    operator fun invoke(
        startDate: Instant,
        endDate: Instant,
        selectedCurrency: AppCurrency
    ): Flow<SpendingIncomeStatus> {
        return transactionRepository.getTransactionsByTimeFrame(startDate, endDate)
            .map { transactions ->
                val totals = transactions.aggregateTotals(selectedCurrency, calculateAmount)

                SpendingIncomeStatus(
                    income = totals.income,
                    spending = totals.spending,
                    isIncomplete = totals.isIncomplete
                )
            }
            .flowOn(Dispatchers.Default)
    }
}