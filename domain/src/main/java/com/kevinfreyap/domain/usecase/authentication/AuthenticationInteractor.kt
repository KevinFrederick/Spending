package com.kevinfreyap.domain.usecase.authentication

import android.app.Activity
import android.util.Patterns
import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.domain.model.AuthenticationRequest
import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.resource.DomainResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthenticationInteractor @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
): AuthenticationUseCase {
    override fun getAuthState(): Flow<Boolean> {
        return authenticationRepository.getAuthState()
    }

    override suspend fun authWithGoogle(activity: Activity): DomainResult<Unit> {
        return authenticationRepository.authWithGoogle(activity)
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return authenticationRepository.isUserLoggedIn()
    }

    override suspend fun register(
        email: String,
        password: String,
        confirmPassword: String
    ): DomainResult<Unit> {
        val validationRes = validateEmailAndPassword(email, password, confirmPassword)

        if (validationRes.isNotEmpty()) {
            return DomainResult.ValidationFailed(validationRes)
        }

        val authRequest = AuthenticationRequest(
            email = email,
            password = password
        )

        return authenticationRepository.register(authRequest)
    }

    override suspend fun login(
        email: String,
        password: String
    ): DomainResult<Unit> {
        val validationRes = validateEmailAndPassword(email, password)

        if (validationRes.isNotEmpty()){
            return DomainResult.ValidationFailed(validationRes)
        }

        val authRequest = AuthenticationRequest(
            email = email,
            password = password
        )

        return authenticationRepository.login(authRequest)
    }

    override suspend fun resetPassword(email: String): DomainResult<Unit> {
        val validationRes = validateResetPasswordEmail(email)

        if (validationRes.isNotEmpty()) {
            return DomainResult.ValidationFailed(validationRes)
        }

        return authenticationRepository.resetPassword(email)
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): DomainResult<Unit> {
        val errors = mutableListOf<ValidationError>()

        validatePassword(oldPassword).forEach {
            errors.add(it)
        }
        validatePassword(newPassword, true).forEach {
            errors.add(it)
        }

        if (errors.isNotEmpty()) {
            return DomainResult.ValidationFailed(errors)
        }

        return authenticationRepository.changePassword(oldPassword, newPassword)
    }

    override suspend fun createPassword(newPassword: String): DomainResult<Unit> {
        val validateNewPassword = validatePassword(newPassword, true)

        if (validateNewPassword.isNotEmpty()) {
            return DomainResult.ValidationFailed(validateNewPassword)
        }

        return authenticationRepository.createPassword(newPassword)
    }

    override suspend fun reauthenticateWithGoogle(activity: Activity): DomainResult<Unit> {
        return authenticationRepository.reauthenticateWithGoogle(activity)
    }

    override suspend fun logout(): DomainResult<Unit> = authenticationRepository.logout()

    override suspend fun deleteAccount(password: String?): DomainResult<Unit> {
        if (password != null) {
            val validatePassword = validatePassword(password)

            if (validatePassword.isNotEmpty()) {
                return DomainResult.ValidationFailed(validatePassword)
            }
        }

        return authenticationRepository.deleteAccount(password)
    }

    private fun validateEmailAndPassword(
        email: String,
        password: String,
        confirmPassword: String? = null
    ): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (email.isBlank()) errors.add(ValidationError.AuthenticationEmailBlank)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) errors.add(ValidationError.AuthenticationEmailWrongFormat)

        validatePassword(password).forEach {
            errors.add(it)
        }

        if (password.isNotBlank() && confirmPassword != null) {
            validatePassword(confirmPassword).forEach {
                errors.add(it)
            }
            if (password != confirmPassword) errors.add(ValidationError.AuthenticationConfirmPasswordNotMatch)
        }

        return errors
    }

    private fun validatePassword(
        password: String,
        isNew: Boolean = false
    ): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (isNew) {
            if (password.isBlank()) errors.add(ValidationError.AuthenticationNewPasswordBlank)
            if (password.length < 8) errors.add(ValidationError.AuthenticationNewPasswordTooShort)
        } else {
            if (password.isBlank()) errors.add(ValidationError.AuthenticationPasswordBlank)
            if (password.length < 8) errors.add(ValidationError.AuthenticationPasswordTooShort)
        }

        return errors
    }

    private fun validateResetPasswordEmail(
        email: String
    ): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (email.isBlank()) errors.add(ValidationError.AuthenticationResetPasswordEmailBlank)
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) errors.add(ValidationError.AuthenticationResetPasswordEmailWrongFormat)

        return errors
    }
}