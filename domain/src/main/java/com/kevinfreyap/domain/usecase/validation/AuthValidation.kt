package com.kevinfreyap.domain.usecase.validation

import android.util.Patterns
import com.kevinfreyap.domain.error.ValidationError

fun validateEmailAndPassword(
    email: String,
    password: String,
    confirmPassword: String? = null,
): List<ValidationError> {
    val errors = mutableListOf<ValidationError>()

    if (email.isBlank()) {
        errors.add(ValidationError.AuthenticationEmailBlank)
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        errors.add(ValidationError.AuthenticationEmailWrongFormat)
    }

    errors.addAll(validatePassword(password))

    if (confirmPassword != null) {
        if (confirmPassword.isBlank()) {
            errors.add(ValidationError.AuthenticationConfirmPasswordBlank)
        } else if (confirmPassword.length < 8) {
            errors.add(ValidationError.AuthenticationConfirmPasswordTooShort)
        }

        if (password.isNotBlank() && confirmPassword.isNotBlank() && (password != confirmPassword)) {
            errors.add(ValidationError.AuthenticationConfirmPasswordNotMatch)
        }
    }

    return errors
}

fun validatePassword(
    password: String,
    isNew: Boolean = false,
): List<ValidationError> {
    val errors = mutableListOf<ValidationError>()

    if (isNew) {
        if (password.isBlank()) {
            errors.add(ValidationError.AuthenticationNewPasswordBlank)
        } else if (password.length < 8) {
            errors.add(ValidationError.AuthenticationNewPasswordTooShort)
        }
    } else {
        if (password.isBlank()) {
            errors.add(ValidationError.AuthenticationPasswordBlank)
        } else if (password.length < 8) {
            errors.add(ValidationError.AuthenticationPasswordTooShort)
        }
    }

    return errors
}

fun validateResetPasswordEmail(
    email: String,
): List<ValidationError> {
    val errors = mutableListOf<ValidationError>()

    if (email.isBlank()) {
        errors.add(ValidationError.AuthenticationResetPasswordEmailBlank)
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        errors.add(ValidationError.AuthenticationResetPasswordEmailWrongFormat)
    }

    return errors
}

fun validateChangePassword(
    oldPassword: String,
    newPassword: String,
): List<ValidationError> {
    val errors = mutableListOf<ValidationError>()

    errors.addAll(validatePassword(oldPassword, isNew = false))
    errors.addAll(validatePassword(newPassword, isNew = true))

    return errors
}
