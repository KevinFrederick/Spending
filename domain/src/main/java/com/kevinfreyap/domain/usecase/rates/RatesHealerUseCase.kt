package com.kevinfreyap.domain.usecase.rates

import android.util.Log
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
    private val ratesRepository: IExchangeRatesRepository
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
                    Log.e("ExchangeRatesInteractor", e.message ?: "Something Wrong")
                }
            }
        }
    }
}