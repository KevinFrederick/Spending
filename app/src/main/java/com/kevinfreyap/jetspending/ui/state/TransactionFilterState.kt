package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.model.FilterTimeOptionUI
import java.time.Instant

data class TransactionFilterState(
    val earliestTransactionYear: Int = 0,
    val timeFilter: TimeFilterOption = TimeFilterOption.ALL_TIME,
    val timeFilterOptions: List<FilterTimeOptionUI> = emptyList(),
    val fromDateRaw: Instant? = null,
    val toDateRaw: Instant? = null,
    val fromDateDisplay: String? = null,
    val toDateDisplay: String? = null,

    val displayFromAmount: String = "",
    val displayToAmount: String = "",
    val fromAmountInput: String = "",
    val toAmountInput: String = "",

    val selectedType: TransactionType? = null,

    val categories: List<CategoryUI> = emptyList(),
    val selectedCategoryId: String? = null,
    val selectedCategoryDisplay: Int? = null
)
