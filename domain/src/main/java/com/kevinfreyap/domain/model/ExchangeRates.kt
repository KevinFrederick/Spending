package com.kevinfreyap.domain.model

data class ExchangeRates (
    val dateKey: String,
    val baseCurrency: AppCurrency,
    val rates: Map<AppCurrency, Double>
)
