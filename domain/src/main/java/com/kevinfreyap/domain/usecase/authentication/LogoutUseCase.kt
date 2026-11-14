package com.kevinfreyap.domain.usecase.authentication

import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.resource.DomainResult
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(): DomainResult<Unit> = authRepository.logout()
}