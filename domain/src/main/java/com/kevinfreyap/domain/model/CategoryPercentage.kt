package com.kevinfreyap.domain.model

import java.math.BigDecimal

data class CategoryPercentage(
    val categoryId: String,
    val categoryIconId: String,
    val type: TransactionType,
    val percentage: Float,
    val amount: BigDecimal,
)
