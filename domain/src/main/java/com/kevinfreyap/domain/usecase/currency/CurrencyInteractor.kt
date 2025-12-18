package com.kevinfreyap.domain.usecase.currency

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.ExchangeRates
import com.kevinfreyap.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.math.MathContext
import javax.inject.Inject

class CurrencyInteractor @Inject constructor(
    private val userRepository: IUserRepository
): CurrencyUseCase {
    override fun getCurrency(): Flow<AppCurrency> = userRepository.getSelectedCurrency()

    override fun calculateAmount(
        amount: BigDecimal,
        sourceCurrency: AppCurrency,
        targetCurrency: AppCurrency,
        rates: ExchangeRates?
    ): BigDecimal? {
        if (rates == null) return null

        // Example: Base = USD | USD to IDR = ...
        val baseCurrencyToSource = rates.rates[sourceCurrency] ?: return null

        // Example: Base = USD | USD to TWD = ...
        val baseCurrencyToTarget = rates.rates[targetCurrency] ?: return null

        if (baseCurrencyToSource == 0.0) return null

        val sourceRateBD = BigDecimal.valueOf(baseCurrencyToSource)
        val targetRateBD = BigDecimal.valueOf(baseCurrencyToTarget)

        // Formula: (Amount / baseCurrencyToSource) * BaseCurrencyToTarget
        val amountInBaseCurrency = amount.divide(sourceRateBD, MathContext.DECIMAL128)
        val finalAmount = amountInBaseCurrency.multiply(targetRateBD)

        return finalAmount
    }

    override suspend fun setCurrency(appCurrency: AppCurrency) {
        userRepository.setCurrency(appCurrency)
    }
}