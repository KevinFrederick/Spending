package com.kevinfreyap.jetspending.ui.screen.list.action

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.screen.list.state.DatePickerTarget

sealed interface FilterLocalAction {
    data class OnSelectDateFilter(val filter: TimeFilterOption): FilterLocalAction
    data class OnSelectCustomStartDate(val dateMillis: Long?): FilterLocalAction
    data class OnSelectCustomEndDate(val dateMillis: Long?): FilterLocalAction
    data class OnSelectType(val type: TransactionType?): FilterLocalAction
    data class OnSelectCurrency(val currency: AppCurrency?): FilterLocalAction
    data class OnSelectCategory(val category: CategoryUI?): FilterLocalAction
    data class OnToggleDateDialog(val target: DatePickerTarget?): FilterLocalAction

    data object ResetFilter: FilterLocalAction
    data object ApplyFilter: FilterLocalAction
}