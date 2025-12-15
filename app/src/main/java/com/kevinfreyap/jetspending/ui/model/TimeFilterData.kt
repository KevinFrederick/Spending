package com.kevinfreyap.jetspending.ui.model

import com.kevinfreyap.domain.model.TimeFilterOption
import java.time.Instant

data class TimeFilterData(
    val earliestTransactionYear: Int,
    val filterType: TimeFilterOption,
    val fromDate: Instant?,
    val toDate: Instant?,
    val displayFromText: String?,
    val displayToText: String?
)
