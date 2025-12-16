package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionType

interface TransactionFilterAction {
    fun onFilterOptionClicked(option: TimeFilterOption)
    fun onDateSelected(millis: Long?, isFrom: Boolean)
    fun onSetSelectedDate()
    fun onResetSelectedDate()
    fun onNavigateToFilter()
    fun onNavigateToAmount(isFrom: Boolean)
    fun onAmountCardClicked(isFrom: Boolean)
    fun onAmountChanged(amount: String, isFrom: Boolean)
    fun onSetAmount(isFrom: Boolean)
    fun onTypeChange(type: TransactionType)
    fun onCategorySelected(categoryId: String)
    fun onApplyFilter()
    fun onResetFilter()
}