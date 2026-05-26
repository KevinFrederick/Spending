package com.kevinfreyap.domain.usecase.privacy_security

import kotlinx.coroutines.flow.Flow

interface PrivacySecurityUseCase {
    fun getAppLockPref(): Flow<Boolean>
    fun getBlockScreenPref(): Flow<Boolean>
    suspend fun setAppLockPref(isEnabled: Boolean)
    suspend fun setBlockScreenPref(isEnabled: Boolean)
}