package com.kevinfreyap.domain.model

import java.math.BigDecimal

data class AggregatedTotals(
    val income: BigDecimal,
    val spending: BigDecimal,
    val isIncomplete: Boolean
) {
    val netBalance: BigDecimal
        get() = income - spending
}
