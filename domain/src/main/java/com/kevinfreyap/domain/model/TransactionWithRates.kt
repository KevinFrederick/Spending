package com.kevinfreyap.domain.model

data class TransactionWithRates(
    val transaction: Transaction,
    val rates: ExchangeRates?
)
