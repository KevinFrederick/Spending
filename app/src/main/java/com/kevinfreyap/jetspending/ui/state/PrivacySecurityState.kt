package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.jetspending.ui.screen.privacy_security.state.PasswordFormState
import com.kevinfreyap.jetspending.ui.screen.privacy_security.state.PrivacySecurityDialogType

data class PrivacySecurityState(
    val isAppLockEnabled: Boolean = false,
    val isScreenshotBlockEnabled: Boolean = false,
    val hasPassword: Boolean = false,
    val userEmail: String = "",
    val resetEmail: String = "",
    val passwordForm: PasswordFormState = PasswordFormState(),
    val activeDialog: PrivacySecurityDialogType = PrivacySecurityDialogType.NONE,
    val uiState: UiState<Unit> = UiState.Idle
)
