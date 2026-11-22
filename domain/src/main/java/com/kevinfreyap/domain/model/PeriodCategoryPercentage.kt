package com.kevinfreyap.domain.model

import java.math.BigDecimal

data class PeriodCategoryPercentage(
    val category: Category,
    val type: TransactionType,
    val percentage: Float,
    val amount: BigDecimal
)
