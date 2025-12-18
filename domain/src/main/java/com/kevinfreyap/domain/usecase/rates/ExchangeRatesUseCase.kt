package com.kevinfreyap.domain.usecase.rates

import com.kevinfreyap.domain.model.ExchangeRates
import kotlinx.coroutines.flow.Flow

interface ExchangeRatesUseCase {
    fun getRatesFlow(dateKey: String): Flow<ExchangeRates?>
    suspend fun ensureRatesExist(dateKey: String)
    suspend fun startRatesHealer()
}