package com.kevinfreyap.jetspending.ui.state

data class PrivacySecurityState(
    val isAppLockEnabled: Boolean = false,
    val isBlockScreenshotEnabled: Boolean = true
)
