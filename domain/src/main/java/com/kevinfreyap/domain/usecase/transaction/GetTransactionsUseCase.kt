package com.kevinfreyap.domain.usecase.transaction

import androidx.paging.PagingData
import androidx.paging.map
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionConverted
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.repository.ITransactionRepository
import com.kevinfreyap.domain.usecase.currency.CalculateAmountBasedOnRatesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository,
    private val calculateAmount: CalculateAmountBasedOnRatesUseCase
) {
    operator fun invoke(
        query: String,
        filter: TransactionFilter,
        selectedCurrency: AppCurrency
    ): Flow<PagingData<TransactionConverted>> {
        return transactionRepository.getTransactions(
            query = query,
            filter = filter
        )
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