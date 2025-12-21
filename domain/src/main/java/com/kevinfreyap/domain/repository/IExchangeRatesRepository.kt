package com.kevinfreyap.domain.repository

import com.kevinfreyap.domain.model.ExchangeRates
import kotlinx.coroutines.flow.Flow

interface IExchangeRatesRepository {
    fun getRatesFlow(dateKey: String): Flow<ExchangeRates?>
    suspend fun ensureRatesExist(dateKey: String)
    suspend fun syncDailyRates()
}