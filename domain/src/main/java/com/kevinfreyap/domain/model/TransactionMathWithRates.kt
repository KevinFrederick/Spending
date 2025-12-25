package com.kevinfreyap.domain.model

import java.math.BigDecimal
import java.time.Instant

data class TransactionMathWithRates(
    val amount: BigDecimal,
    val currency: AppCurrency,
    val type: TransactionType,
    val category: Category,
    val date: Instant,
    val stringDate: String,
    val rates: ExchangeRates?
)
