package com.kevinfreyap.domain.repository

import com.kevinfreyap.domain.model.ExchangeRates

interface IExchangeRatesRepository {
    suspend fun getRates(dateKey: String): ExchangeRates?
    suspend fun ensureRatesExist(dateKey: String)
    suspend fun syncDailyRates()
}