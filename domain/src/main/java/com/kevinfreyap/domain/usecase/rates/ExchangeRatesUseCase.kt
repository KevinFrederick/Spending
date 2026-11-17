package com.kevinfreyap.domain.usecase.rates

interface ExchangeRatesUseCase {
    suspend fun ensureRatesExist(dateKey: String)
    suspend fun startRatesHealer()
    suspend fun syncDailyRates()
}