package com.kevinfreyap.jetspending.utils.formatter

import com.kevinfreyap.domain.error.ErrorMessage
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.jetspending.R

object ErrorFormatter {
    fun getErrorMessage(messageCode: ErrorMessage): Int {
        return when(messageCode) {
            ErrorMessage.TRANSACTION_NAME_REQUIRED ->R.string.error_required_transaction_name
            ErrorMessage.TRANSACTION_AMOUNT_ZERO -> R.string.error_required_transaction_amount
            ErrorMessage.TRANSACTION_CATEGORY_NOT_SELECTED -> R.string.error_required_transaction_category
            ErrorMessage.AUTHENTICATION_EMAIL_BLANK -> R.string.error_authentication_email_blank
            ErrorMessage.AUTHENTICATION_EMAIL_WRONG_FORMAT -> R.string.error_authentication_email_wrong_format
            ErrorMessage.AUTHENTICATION_EMAIL_USED -> R.string.error_authentication_email_used
            ErrorMessage.AUTHENTICATION_PASSWORD_BLANK -> R.string.error_authentication_password_blank
            ErrorMessage.AUTHENTICATION_PASSWORD_TOO_SHORT -> R.string.error_authentication_password_too_short
            ErrorMessage.AUTHENTICATION_PASSWORD_WRONG -> R.string.error_authentication_password_wrong
            ErrorMessage.AUTHENTICATION_CONFIRM_PASSWORD_BLANK -> R.string.error_authentication_confirm_password_blank
            ErrorMessage.AUTHENTICATION_CONFIRM_PASSWORD_NOT_MATCH -> R.string.error_authentication_confirm_password_not_match
        }
    }
}