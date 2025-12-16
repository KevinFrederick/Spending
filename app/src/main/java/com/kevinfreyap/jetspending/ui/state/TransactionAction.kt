package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.TransactionType

interface TransactionAction {
    fun onNameChange(name: String)
    fun onAmountChange(amount: String)
    fun onSetAmount()
    fun initializeAmount()
    fun onSelectType(type: TransactionType)
    fun onSelectCategory(categoryId: String)
    fun onDateSelected(millis: Long?)
    fun onSaveTransaction()
    fun onDismissSuccessDialog()
}