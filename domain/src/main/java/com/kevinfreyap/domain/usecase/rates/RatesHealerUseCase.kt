package com.kevinfreyap.domain.usecase.rates

import com.kevinfreyap.domain.error.ErrorReporter
import com.kevinfreyap.domain.repository.IConnectivityRepository
import com.kevinfreyap.domain.repository.IExchangeRatesRepository
import com.kevinfreyap.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import java.lang.Exception
import javax.inject.Inject

class RatesHealerUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository,
    private val connectivityRepository: IConnectivityRepository,
    private val ratesRepository: IExchangeRatesRepository,
    private val errorReporter: ErrorReporter,
) {
    suspend operator fun invoke() {
        combine(
            transactionRepository.getDatesOfMissingRates()
                .distinctUntilChanged(),
            connectivityRepository.isOnline
        ) { transactions, isOnline ->
            Pair(transactions, isOnline)
        }.collect { (dates, isOnline) ->
            if (!isOnline || dates.isEmpty()) return@collect

            dates.forEach { date ->
                try {
                    ratesRepository.ensureRatesExist(date)
                } catch (e: Exception) {
                    errorReporter.setCustomKey("rate_heal_date", date)
                    errorReporter.recordException(e)
                }
            }
        }
    }
}