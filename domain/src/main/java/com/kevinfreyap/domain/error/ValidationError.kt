package com.kevinfreyap.domain.error

sealed class ValidationError(
    val field: Field,
    val message: ErrorMessage
) {
    object TransactionNameRequired: ValidationError(
        Field.TRANSACTION_NAME,
        ErrorMessage.TRANSACTION_NAME_REQUIRED
    )
    object TransactionAmountInvalid: ValidationError(
        Field.TRANSACTION_AMOUNT,
        ErrorMessage.TRANSACTION_AMOUNT_ZERO
    )
    object TransactionAmountFromGreaterThanTo: ValidationError(
        Field.TRANSACTION_AMOUNT,
        ErrorMessage.TRANSACTION_AMOUNT_FROM_GREATER_THAN_TO
    )
    object TransactionCurrencyNotValid: ValidationError(
        Field.TRANSACTION_AMOUNT,
        ErrorMessage.TRANSACTION_CURRENCY_NOT_VALID
    )
    object TransactionCategoryMissing: ValidationError(
        Field.TRANSACTION_CATEGORY,
        ErrorMessage.TRANSACTION_CATEGORY_NOT_SELECTED
    )
    object TransactionNotesTooLong: ValidationError(
        Field.TRANSACTION_NOTES,
        ErrorMessage.TRANSACTION_NOTES_TOO_LONG
    )

    object AuthenticationEmailBlank: ValidationError(
        Field.AUTHENTICATION_EMAIL,
        ErrorMessage.AUTHENTICATION_EMAIL_BLANK
    )
    object AuthenticationEmailWrongFormat: ValidationError(
        Field.AUTHENTICATION_EMAIL,
        ErrorMessage.AUTHENTICATION_EMAIL_WRONG_FORMAT
    )
    object AuthenticationEmailAlreadyUsed: ValidationError(
        Field.AUTHENTICATION_EMAIL,
        ErrorMessage.AUTHENTICATION_EMAIL_USED
    )
    object AuthenticationPasswordBlank: ValidationError(
        Field.AUTHENTICATION_PASSWORD,
        ErrorMessage.AUTHENTICATION_PASSWORD_BLANK
    )
    object AuthenticationPasswordTooShort: ValidationError(
        Field.AUTHENTICATION_PASSWORD,
        ErrorMessage.AUTHENTICATION_PASSWORD_TOO_SHORT
    )
    object AuthenticationWrongPassword: ValidationError(
        Field.AUTHENTICATION_PASSWORD,
        ErrorMessage.AUTHENTICATION_PASSWORD_WRONG
    )
    object AuthenticationConfirmPasswordBlank: ValidationError(
        Field.AUTHENTICATION_CONFIRM_PASSWORD,
        ErrorMessage.AUTHENTICATION_CONFIRM_PASSWORD_BLANK
    )
    object AuthenticationConfirmPasswordTooShort: ValidationError(
        Field.AUTHENTICATION_CONFIRM_PASSWORD,
        ErrorMessage.AUTHENTICATION_PASSWORD_TOO_SHORT
    )
    object AuthenticationConfirmPasswordNotMatch: ValidationError(
        Field.AUTHENTICATION_CONFIRM_PASSWORD,
        ErrorMessage.AUTHENTICATION_CONFIRM_PASSWORD_NOT_MATCH
    )
    object AuthenticationResetPasswordEmailBlank: ValidationError(
        Field.AUTHENTICATION_CHANGE_PASSWORD_EMAIL,
        ErrorMessage.AUTHENTICATION_EMAIL_BLANK
    )
    object AuthenticationResetPasswordEmailWrongFormat: ValidationError(
        Field.AUTHENTICATION_CHANGE_PASSWORD_EMAIL,
        ErrorMessage.AUTHENTICATION_EMAIL_WRONG_FORMAT
    )
}
