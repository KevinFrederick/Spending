package com.kevinfreyap.domain.usecase.authentication

import com.kevinfreyap.domain.repository.IAuthenticationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthStatusUseCase @Inject constructor(
    private val authRepository: IAuthenticationRepository
) {
    operator fun invoke(): Flow<Boolean> =
        authRepository.getAuthState()
}