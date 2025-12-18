package com.kevinfreyap.domain.usecase.rates

import android.util.Log
import com.kevinfreyap.domain.model.ExchangeRates
import com.kevinfreyap.domain.repository.IExchangeRatesRepository
import com.kevinfreyap.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import java.lang.Exception
import javax.inject.Inject

class ExchangeRatesInteractor @Inject constructor(
    private val transactionRepository: ITransactionRepository,
    private val exchangeRatesRepository: IExchangeRatesRepository
): ExchangeRatesUseCase {
    override fun getRatesFlow(dateKey: String): Flow<ExchangeRates?> {
        return exchangeRatesRepository.getRatesFlow(dateKey)
    }

    override suspend fun ensureRatesExist(dateKey: String) {
        exchangeRatesRepository.ensureRatesExist(dateKey)
    }

    override suspend fun startRatesHealer() {
        transactionRepository.getDatesOfMissingRates().collect { dates ->
            if (dates.isEmpty()) return@collect

            dates.forEach { date ->
                try {
                    exchangeRatesRepository.ensureRatesExist(date)
                } catch (e: Exception) {
                    Log.e("ExchangeRatesInteractor", e.message ?: "Something Wrong")
                }
            }
        }
    }
}