package com.kevinfreyap.domain.model

import java.math.BigDecimal

data class SpendingIncomeStatus (
    val income: BigDecimal,
    val spending: BigDecimal,
    val isIncomplete: Boolean
)