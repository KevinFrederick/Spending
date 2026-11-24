package com.kevinfreyap.domain.usecase.authentication

import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.validation.validateResetPasswordEmail
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(email: String): DomainResult<Unit> {
        val validationErrors = validateResetPasswordEmail(email)
        if (validationErrors.isNotEmpty()) {
            return DomainResult.ValidationFailed(validationErrors)
        }
        return authenticationRepository.resetPassword(email)
    }
}
