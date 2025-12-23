package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import java.math.BigDecimal
import java.time.Instant

data class TransactionDraft (
    val transactionAmountRaw: BigDecimal = BigDecimal.ZERO,
    val transactionCurrency: AppCurrency? = null,
    val transactionType: TransactionType = TransactionType.SPENDING,
    val transactionCategoryId: String? = null,
    val transactionDate: Instant = Instant.now()
)