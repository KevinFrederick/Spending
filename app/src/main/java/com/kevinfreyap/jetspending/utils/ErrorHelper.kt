package com.kevinfreyap.jetspending.utils

import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.jetspending.ui.model.UiState
import com.kevinfreyap.jetspending.utils.formatter.ErrorFormatter.getErrorMessage

object ErrorHelper {
    fun validationErrorsToUiError(errors: List<ValidationError>): Map<Field, Int> {
        return errors.associate { validationError ->
            validationError.field to getErrorMessage(validationError.message)
        }
    }

    fun <T> removeError(currentState: UiState<T>, field: Field): UiState<T> {
        if (currentState is UiState.ValidationErrors &&
            currentState.errors.containsKey(field)){
            val updatedErrors = currentState.errors.toMutableMap().apply {
                remove(field)
            }
            return UiState.ValidationErrors(updatedErrors)
        } else {
            return currentState
        }
    }
}