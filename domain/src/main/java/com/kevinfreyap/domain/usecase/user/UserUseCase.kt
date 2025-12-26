package com.kevinfreyap.domain.usecase.user

import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserUseCase {
    fun getUserProfile(): Flow<User?>
    fun getCurrentTheme(): Flow<AppTheme>
    suspend fun setTheme(appTheme: AppTheme)
}