package com.kevinfreyap.domain.repository

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun getUserProfile(): Flow<User?>
    fun getSelectedCurrency(): Flow<AppCurrency>
    fun getCurrentTheme(): Flow<AppTheme>
    suspend fun setCurrency(appCurrency: AppCurrency)
    suspend fun setTheme(appTheme: AppTheme)
}