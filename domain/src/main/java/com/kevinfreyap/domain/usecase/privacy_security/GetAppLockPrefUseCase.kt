package com.kevinfreyap.domain.usecase.privacy_security

import com.kevinfreyap.domain.repository.IPrivacySecurityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppLockPrefUseCase @Inject constructor(
    private val privacySecurityRepository: IPrivacySecurityRepository
) {
    operator fun invoke(): Flow<Boolean> =
        privacySecurityRepository.getAppLockPreference()
}