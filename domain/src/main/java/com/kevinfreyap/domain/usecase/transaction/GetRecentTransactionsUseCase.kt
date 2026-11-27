package com.kevinfreyap.domain.usecase.transaction

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionConverted
import com.kevinfreyap.domain.repository.ITransactionRepository
import com.kevinfreyap.domain.usecase.currency.CalculateAmountBasedOnRatesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRecentTransactionsUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository,
    private val calculateAmount: CalculateAmountBasedOnRatesUseCase
) {
    operator fun invoke(
        limit: Int = 3,
        selectedCurrency: AppCurrency
    ): Flow<List<TransactionConverted>> {
        return transactionRepository.getLatestTransactions(limit)
            .map { list ->
                list.map { transactionWithRates ->
                    val convertedAmount = calculateAmount(
                        amount = transactionWithRates.transaction.amount,
                        sourceCurrency = transactionWithRates.transaction.currency,
                        targetCurrency = selectedCurrency,
                        rates = transactionWithRates.rates
                    )

                    TransactionConverted(
                        transaction = transactionWithRates.transaction,
                        convertedAmount = convertedAmount
                    )
                }
            }
    }
}