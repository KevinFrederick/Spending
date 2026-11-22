package com.kevinfreyap.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class PeriodBucket(
    val id: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val expense: BigDecimal,
    val income: BigDecimal
)
