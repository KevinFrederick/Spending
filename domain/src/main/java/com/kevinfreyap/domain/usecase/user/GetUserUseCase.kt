package com.kevinfreyap.domain.usecase.user

import com.kevinfreyap.domain.model.User
import com.kevinfreyap.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    operator fun invoke(): Flow<User?> =
        userRepository.getUserProfile()
            .distinctUntilChanged()
}