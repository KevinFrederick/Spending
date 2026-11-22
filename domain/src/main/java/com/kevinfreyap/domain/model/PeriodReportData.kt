package com.kevinfreyap.domain.model

import java.math.BigDecimal

data class PeriodReportData(
    val totalExpense: BigDecimal,
    val totalIncome: BigDecimal,
    val net: BigDecimal,
    val buckets: List<PeriodBucket>,
    val categories: List<PeriodCategoryPercentage>,
    val hasMissingRates: Boolean,
)
