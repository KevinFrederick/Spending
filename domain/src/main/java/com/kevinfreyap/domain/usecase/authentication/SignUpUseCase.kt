package com.kevinfreyap.domain.usecase.authentication

import com.kevinfreyap.domain.model.AuthenticationRequest
import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.validation.validateEmailAndPassword
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): DomainResult<Unit> {
        val validationErrors = validateEmailAndPassword(email, password, confirmPassword)
        if (validationErrors.isNotEmpty()) {
            return DomainResult.ValidationFailed(validationErrors)
        }

        val authRequest = AuthenticationRequest(
            email = email,
            password = password
        )

        return authenticationRepository.register(authRequest)
    }
}
