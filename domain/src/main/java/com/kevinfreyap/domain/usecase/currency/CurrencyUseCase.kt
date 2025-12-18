package com.kevinfreyap.domain.usecase.currency

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.ExchangeRates
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface CurrencyUseCase {
    fun getCurrency(): Flow<AppCurrency>
    fun calculateAmount(
        amount: BigDecimal,
        sourceCurrency: AppCurrency,
        targetCurrency: AppCurrency,
        rates: ExchangeRates?
    ): BigDecimal?
    suspend fun setCurrency(appCurrency: AppCurrency)
}