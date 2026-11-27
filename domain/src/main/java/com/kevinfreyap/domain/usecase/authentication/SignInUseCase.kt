package com.kevinfreyap.domain.usecase.authentication

import com.kevinfreyap.domain.model.AuthenticationRequest
import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.validation.validateEmailAndPassword
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): DomainResult<Unit> {
        val validationErrors = validateEmailAndPassword(email, password)
        if (validationErrors.isNotEmpty()) {
            return DomainResult.ValidationFailed(validationErrors)
        }

        val authRequest = AuthenticationRequest(
            email = email,
            password = password
        )

        return authenticationRepository.login(authRequest)
    }
}
