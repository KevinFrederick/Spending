package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionType

interface TransactionFilterAction {
    fun onFilterOptionClicked(option: TimeFilterOption)
    fun onFromDateSelected(millis: Long?)
    fun onToDateSelected(millis: Long?)
    fun onSetSelectedDate()
    fun onResetSelectedDate()
    fun onNavigateToFilter()
    fun onFromAmountChanged(amount: String)
    fun onToAmountChanged(amount: String)
    fun onTypeChange(type: TransactionType)
    fun onCategorySelected(categoryId: String)
    fun onFromPositiveBtnClicked()
    fun onToPositiveBtnClicked()
    fun onApplyFilter()
    fun onResetFilter()
}