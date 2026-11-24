package com.kevinfreyap.domain.usecase.privacy_security

import com.kevinfreyap.domain.repository.IPrivacySecurityRepository
import javax.inject.Inject

class SetBlockScreenshotPrefUseCase @Inject constructor(
    private val privacySecurityRepository: IPrivacySecurityRepository
) {
    suspend operator fun invoke(isEnabled: Boolean) =
        privacySecurityRepository.setBlockScreenPreference(isEnabled)
}