package com.kevinfreyap.domain.usecase.authentication

import com.kevinfreyap.domain.model.AuthenticationRequest
import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.validation.validateChangePassword
import com.kevinfreyap.domain.usecase.validation.validateEmailAndPassword
import com.kevinfreyap.domain.usecase.validation.validatePassword
import com.kevinfreyap.domain.usecase.validation.validateResetPasswordEmail
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthenticationInteractor @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
): AuthenticationUseCase {
    override fun getAuthState(): Flow<Boolean> {
        return authenticationRepository.getAuthState()
    }

    override suspend fun authWithGoogle(idToken: String): DomainResult<Unit> {
        return authenticationRepository.authWithGoogle(idToken)
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
        val errors = validateChangePassword(oldPassword, newPassword)

        if (errors.isNotEmpty()) {
            return DomainResult.ValidationFailed(errors)
        }

        return authenticationRepository.changePassword(oldPassword, newPassword)
    }

    override suspend fun createPassword(newPassword: String): DomainResult<Unit> {
        val validateNewPassword = validatePassword(newPassword, isNew = true)

        if (validateNewPassword.isNotEmpty()) {
            return DomainResult.ValidationFailed(validateNewPassword)
        }

        return authenticationRepository.createPassword(newPassword)
    }

    override suspend fun reauthenticateWithGoogle(idToken: String): DomainResult<Unit> {
        return authenticationRepository.reauthenticateWithGoogle(idToken)
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
}
