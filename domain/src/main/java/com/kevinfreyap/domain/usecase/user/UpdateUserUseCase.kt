package com.kevinfreyap.domain.usecase.user

import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.domain.repository.IUserRepository
import com.kevinfreyap.domain.resource.DomainResult
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(
        userName: String?,
    ): DomainResult<Unit> {
        if (userName.isNullOrBlank()) {
            return DomainResult.ValidationFailed(listOf(ValidationError.UsernameBlank))
        }

        val currentUser = userRepository.getUserProfile().firstOrNull()
            ?: return DomainResult.Failure(IllegalStateException("User not logged in"))

        return userRepository.updateUserProfile(
            userId = currentUser.uid,
            newUsername = userName,
        )
    }
}