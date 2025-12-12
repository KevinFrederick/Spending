package com.kevinfreyap.jetspending.utils

import com.kevinfreyap.domain.error.ValidationError

object AuthUiValidation {
    fun validateEmailAndPassword(email: String, password: String, confirmPassword: String? = null): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (email.isBlank()) errors.add(ValidationError.AuthenticationEmailBlank)
        if (password.isBlank()) errors.add(ValidationError.AuthenticationPasswordBlank)
        if (password.isNotBlank() && confirmPassword != null) {
            if (confirmPassword.isBlank()) errors.add(ValidationError.AuthenticationConfirmPasswordBlank)
        }

        return errors
    }
}