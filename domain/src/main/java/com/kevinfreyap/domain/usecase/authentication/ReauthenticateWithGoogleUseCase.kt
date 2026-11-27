package com.kevinfreyap.domain.usecase.authentication

import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.resource.DomainResult
import javax.inject.Inject

class ReauthenticateWithGoogleUseCase @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(idToken: String): DomainResult<Unit> {
        return authenticationRepository.reauthenticateWithGoogle(idToken)
    }
}
