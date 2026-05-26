package com.kevinfreyap.data.repository

import com.kevinfreyap.data.source.local.UserPreferences
import com.kevinfreyap.domain.repository.IPrivacySecurityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PrivacySecurityRepository @Inject constructor(
    private val userPreferences: UserPreferences
): IPrivacySecurityRepository {
    override fun getAppLockPreference(): Flow<Boolean> = userPreferences.getAppLockPreference()

    override fun getBlockScreenPreference(): Flow<Boolean> = userPreferences.getBlockScreenPreference()

    override suspend fun setAppLockPreference(isEnabled: Boolean) {
        userPreferences.saveAppLockPreference(isEnabled)
    }

    override suspend fun setBlockScreenPreference(isEnabled: Boolean) {
        userPreferences.saveBlockScreenPreference(isEnabled)
    }
}