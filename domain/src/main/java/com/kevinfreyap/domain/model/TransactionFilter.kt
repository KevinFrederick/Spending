package com.kevinfreyap.domain.model

import java.time.Instant

data class TransactionFilter(
    val timeFilter: TimeFilterOption = TimeFilterOption.ALL_TIME,
    val customStartDate: Instant? = null,
    val customEndDate: Instant? = null,
    val currency: AppCurrency? = null,
    val type: TransactionType? = null,
    val category: String? = null
)
