package com.kevinfreyap.domain.usecase.user

import com.kevinfreyap.domain.model.User
import com.kevinfreyap.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserInteractor @Inject constructor(
    private val userRepository: IUserRepository
): UserUseCase {
    override fun getUserProfile(): Flow<User?> = userRepository.getUserProfile()
}