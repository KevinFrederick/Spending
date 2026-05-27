package com.kevinfreyap.domain.usecase.user

import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.domain.model.User
import com.kevinfreyap.domain.repository.IUserRepository
import com.kevinfreyap.domain.resource.DomainResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserInteractor @Inject constructor(
    private val userRepository: IUserRepository
): UserUseCase {
    override fun getUserProfile(): Flow<User?> = userRepository.getUserProfile()

    override fun getCurrentTheme(): Flow<AppTheme> = userRepository.getCurrentTheme()

    override suspend fun setTheme(appTheme: AppTheme) {
        userRepository.setTheme(appTheme)
    }

    override suspend fun updateUserProfile(
        userId: String,
        newUsername: String?,
        newImageUrl: String?
    ): DomainResult<Unit> {
        if (newUsername.isNullOrBlank()) {
            return DomainResult.ValidationFailed(listOf(ValidationError.UsernameBlank))
        }

        if (newImageUrl.isNullOrBlank()) {
            return DomainResult.ValidationFailed(listOf(ValidationError.ImageUrlBlank))
        }

        return try {
            userRepository.updateUserProfile(
                userId = userId,
                newUsername = newUsername,
                newImageUrl = newImageUrl
            )
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(e)
        }
    }
}