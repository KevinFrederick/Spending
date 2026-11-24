package com.kevinfreyap.domain.usecase.authentication

import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.validation.validateChangePassword
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(
        oldPassword: String,
        newPassword: String
    ): DomainResult<Unit> {
        val validationErrors = validateChangePassword(oldPassword, newPassword)
        if (validationErrors.isNotEmpty()) {
            return DomainResult.ValidationFailed(validationErrors)
        }
        return authenticationRepository.changePassword(oldPassword, newPassword)
    }
}
