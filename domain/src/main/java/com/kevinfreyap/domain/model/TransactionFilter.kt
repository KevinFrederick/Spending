package com.kevinfreyap.domain.model

import java.math.BigDecimal
import java.time.Instant

data class TransactionFilter(
    val timeFilter: TimeFilterOption = TimeFilterOption.ALL_TIME,
    val customStartDate: Instant? = null,
    val customEndDate: Instant? = null,
    val fromAmount: BigDecimal = BigDecimal.ZERO,
    val toAmount: BigDecimal = BigDecimal.ZERO,
    val type: TransactionType? = null,
    val category: String? = null
) {
    val hasActiveFilters: Boolean
        get() = (timeFilter != TimeFilterOption.ALL_TIME) ||
                (customStartDate != null) ||
                (customEndDate != null) ||
                (fromAmount != BigDecimal.ZERO) ||
                (toAmount != BigDecimal.ZERO) ||
                (type != null) ||
                (category != null)
}
