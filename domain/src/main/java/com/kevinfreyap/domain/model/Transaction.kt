package com.kevinfreyap.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val amount: BigDecimal,
    val currency: AppCurrency,
    val type: TransactionType,
    val category: Category,
    val date: Instant,
)
