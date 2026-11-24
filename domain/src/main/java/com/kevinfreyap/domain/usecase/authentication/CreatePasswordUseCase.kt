package com.kevinfreyap.domain.usecase.authentication

import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.validation.validatePassword
import javax.inject.Inject

class CreatePasswordUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(newPassword: String): DomainResult<Unit> {
        val validationErrors = validatePassword(newPassword, isNew = true)
        if (validationErrors.isNotEmpty()) {
            return DomainResult.ValidationFailed(validationErrors)
        }
        return authenticationRepository.createPassword(newPassword)
    }
}
