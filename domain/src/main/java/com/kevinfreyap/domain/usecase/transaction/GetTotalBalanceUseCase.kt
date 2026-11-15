package com.kevinfreyap.domain.usecase.transaction

import com.kevinfreyap.domain.helper.aggregateTotals
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TotalBalanceStatus
import com.kevinfreyap.domain.repository.ITransactionRepository
import com.kevinfreyap.domain.usecase.currency.CalculateAmountBasedOnRatesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTotalBalanceUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository,
    private val calculateAmount: CalculateAmountBasedOnRatesUseCase
) {
    operator fun invoke(
        selectedCurrency: AppCurrency
    ): Flow<TotalBalanceStatus> {
        return transactionRepository.getAllTimeTransactions()
            .map { transactions ->
                val totals = transactions.aggregateTotals(selectedCurrency, calculateAmount)

                TotalBalanceStatus(
                    totalBalance = totals.netBalance,
                    isIncomplete = totals.isIncomplete
                )
            }.flowOn(Dispatchers.Default)
    }
}