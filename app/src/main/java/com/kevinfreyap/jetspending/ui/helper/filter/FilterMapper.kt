package com.kevinfreyap.jetspending.ui.helper.filter

import androidx.annotation.StringRes
import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.screen.list.state.FilterState

@StringRes
fun TimeFilterOption.toUINameRes(): Int {
    return when(this) {
        TimeFilterOption.ALL_TIME -> R.string.all_time
        TimeFilterOption.LAST_7_DAYS -> R.string.last_7_days
        TimeFilterOption.THIS_MONTH -> R.string.this_month
        TimeFilterOption.PICK_DATE -> R.string.pick_date
    }
}

@StringRes
fun TransactionType?.toUiNameRes(): Int {
    return when(this) {
        TransactionType.SPENDING -> R.string.expense
        TransactionType.INCOME -> R.string.income
        else -> R.string.all
    }
}

fun FilterState.toDomain(): TransactionFilter =
    TransactionFilter(
        timeFilter = this.filterDateState.selectedOption,
        customStartDate = this.filterDateState.customStartDate,
        customEndDate = this.filterDateState.customEndDate,
        type = this.filterType,
        currency = this.filterCurrency,
        category = this.filterCategory?.id
    )