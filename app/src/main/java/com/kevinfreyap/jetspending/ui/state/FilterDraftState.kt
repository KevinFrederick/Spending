package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionType
import java.math.BigDecimal
import java.time.Instant

data class FilterDraftState (
    val timeFilter: TimeFilterOption = TimeFilterOption.ALL_TIME,
    // Temporary selected date
    val tempStartDate: Instant? = null,
    val tempEndDate: Instant? = null,
    // Confirmed selected date
    val startDate: Instant? = null,
    val endDate: Instant? = null,

    // Amount
    val fromAmount: BigDecimal = BigDecimal.ZERO,
    val toAmount: BigDecimal = BigDecimal.ZERO,

    // Type
    val selectedType: TransactionType? = null,
    val selectedCategoryId: String? = null
)