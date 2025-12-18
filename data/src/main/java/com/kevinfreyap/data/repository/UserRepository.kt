package com.kevinfreyap.data.repository

import com.kevinfreyap.data.source.local.UserPreferences
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.User
import com.kevinfreyap.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userPreferences: UserPreferences
): IUserRepository {
    override fun getUserProfile(): Flow<User?> = userPreferences.getUser()
    override fun getSelectedCurrency(): Flow<AppCurrency> = userPreferences.getCurrency()
    override suspend fun setCurrency(appCurrency: AppCurrency) {
        userPreferences.saveCurrency(appCurrency)
    }
}