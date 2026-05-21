package com.kevinfreyap.domain.usecase.user

import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.domain.model.User
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.resource.Resource
import kotlinx.coroutines.flow.Flow

interface UserUseCase {
    fun getUserProfile(): Flow<User?>
    fun getCurrentTheme(): Flow<AppTheme>
    suspend fun setTheme(appTheme: AppTheme)
    suspend fun updateUserProfile(
        userId: String,
        newUsername: String?,
        newImageUrl: String?
    ): DomainResult<Unit>
}