package com.kevinfreyap.domain.usecase.rates

import android.util.Log
import com.kevinfreyap.domain.model.ExchangeRates
import com.kevinfreyap.domain.repository.IConnectivityRepository
import com.kevinfreyap.domain.repository.IExchangeRatesRepository
import com.kevinfreyap.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import java.lang.Exception
import javax.inject.Inject

class ExchangeRatesInteractor @Inject constructor(
    private val transactionRepository: ITransactionRepository,
    private val exchangeRatesRepository: IExchangeRatesRepository,
    private val connectivityRepository: IConnectivityRepository
): ExchangeRatesUseCase {
    override fun getRatesFlow(dateKey: String): Flow<ExchangeRates?> {
        return exchangeRatesRepository.getRatesFlow(dateKey)
    }

    override suspend fun ensureRatesExist(dateKey: String) {
        exchangeRatesRepository.ensureRatesExist(dateKey)
    }

    override suspend fun startRatesHealer() {
        combine(
            transactionRepository.getDatesOfMissingRates()
                .distinctUntilChanged(),
            connectivityRepository.isOnline
        ) { transactions, isOnline ->
            Log.d("ExchangeInteractor", transactions.size.toString())

            Pair(transactions, isOnline)
        }.collect { (dates, isOnline) ->
            if (!isOnline || dates.isEmpty()) return@collect

            dates.forEach { date ->
                try {
                    exchangeRatesRepository.ensureRatesExist(date)
                } catch (e: Exception) {
                    Log.e("ExchangeRatesInteractor", e.message ?: "Something Wrong")
                }
            }
        }
    }

    override suspend fun syncDailyRates() {
        exchangeRatesRepository.syncDailyRates()
    }
}