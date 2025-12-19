package com.kevinfreyap.domain.model

import java.math.BigDecimal

data class MonthlyStatus (
    val monthlyIncome: BigDecimal,
    val monthlySpending: BigDecimal,
    val isIncomplete: Boolean
)