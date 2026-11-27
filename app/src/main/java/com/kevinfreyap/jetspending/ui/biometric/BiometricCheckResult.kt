package com.kevinfreyap.jetspending.ui.biometric

sealed interface BiometricCheckResult {
    data object Ready: BiometricCheckResult
    data object NotEnrolled: BiometricCheckResult
    data object Unavailable: BiometricCheckResult
}