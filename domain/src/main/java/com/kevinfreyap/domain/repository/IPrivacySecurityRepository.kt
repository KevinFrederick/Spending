package com.kevinfreyap.domain.repository

import kotlinx.coroutines.flow.Flow

interface IPrivacySecurityRepository {
    fun getAppLockPreference(): Flow<Boolean>
    fun getBlockScreenPreference(): Flow<Boolean>
    suspend fun setAppLockPreference(isEnabled: Boolean)
    suspend fun setBlockScreenPreference(isEnabled: Boolean)
}