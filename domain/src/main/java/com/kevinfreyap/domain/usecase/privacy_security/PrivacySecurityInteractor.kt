package com.kevinfreyap.domain.usecase.privacy_security

import com.kevinfreyap.domain.repository.IPrivacySecurityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PrivacySecurityInteractor @Inject constructor(
    private val privacySecurityRepository: IPrivacySecurityRepository
): PrivacySecurityUseCase {
    override fun getAppLockPref(): Flow<Boolean> = privacySecurityRepository.getAppLockPreference()

    override fun getBlockScreenPref(): Flow<Boolean> = privacySecurityRepository.getBlockScreenPreference()

    override suspend fun setAppLockPref(isEnabled: Boolean) {
        privacySecurityRepository.setAppLockPreference(isEnabled)
    }

    override suspend fun setBlockScreenPref(isEnabled: Boolean) {
        privacySecurityRepository.setBlockScreenPreference(isEnabled)
    }

}