package com.kevinfreyap.jetspending.utils.formatter

import com.kevinfreyap.domain.error.ErrorMessage
import com.kevinfreyap.jetspending.R

object ErrorFormatter {

    fun getErrorMessage(messageCode: ErrorMessage): Int {
        return when(messageCode) {
            ErrorMessage.TRANSACTION_NAME_REQUIRED ->R.string.error_required_transaction_name
            ErrorMessage.TRANSACTION_AMOUNT_ZERO -> R.string.error_required_transaction_amount
            ErrorMessage.TRANSACTION_CATEGORY_NOT_SELECTED -> R.string.error_required_transaction_category
        }
    }
}