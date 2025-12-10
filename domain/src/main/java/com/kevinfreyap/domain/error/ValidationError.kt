package com.kevinfreyap.domain.error

sealed class ValidationError(
    val field: Field,
    val message: ErrorMessage
) {
    object TransactionNameRequired: ValidationError(Field.TRANSACTION_NAME, ErrorMessage.TRANSACTION_NAME_REQUIRED)
    object TransactionAmountInvalid: ValidationError(Field.TRANSACTION_AMOUNT, ErrorMessage.TRANSACTION_AMOUNT_ZERO)
    object TransactionCategoryMissing: ValidationError(Field.TRANSACTION_CATEGORY, ErrorMessage.TRANSACTION_CATEGORY_NOT_SELECTED)
}
