package com.kevinfreyap.jetspending.ui.screen.list.state

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.model.CategoryUI

data class FilterState(
    val filterDateState: FilterDateState = FilterDateState(),
    val filterType: TransactionType? = null,
    val filterCurrency: AppCurrency? = null,
    val filterCategory: CategoryUI? = null,
) {
    val isValid: Boolean
        get() = !filterDateState.isCustomRangeInvalid

    val hasActiveFilter: Boolean
        get() = filterDateState != FilterDateState() ||
                filterType != null ||
                filterCurrency != null ||
                filterCategory != null
}
