package com.kevinfreyap.domain.usecase.authentication

import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.validation.validatePassword
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(password: String? = null): DomainResult<Unit> {
        if (password != null) {
            val validationErrors = validatePassword(password)
            if (validationErrors.isNotEmpty()) {
                return DomainResult.ValidationFailed(validationErrors)
            }
        }
        return authenticationRepository.deleteAccount(password)
    }
}
