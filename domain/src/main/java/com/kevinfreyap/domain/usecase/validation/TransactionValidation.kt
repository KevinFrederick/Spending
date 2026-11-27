package com.kevinfreyap.domain.usecase.validation

import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.domain.model.Category
import java.math.BigDecimal

fun validateTransaction(
    name: String,
    amount: BigDecimal,
    category: Category?,
    notes: String
): List<ValidationError> {
    val errors = mutableListOf<ValidationError>()

    if (name.isBlank()) errors.add(ValidationError.TransactionNameRequired)
    if (amount <= BigDecimal.ZERO) errors.add(ValidationError.TransactionAmountInvalid)
    if (category == null) errors.add(ValidationError.TransactionCategoryMissing)
    if (notes.length > 1000) errors.add(ValidationError.TransactionNotesTooLong)

    return errors
}