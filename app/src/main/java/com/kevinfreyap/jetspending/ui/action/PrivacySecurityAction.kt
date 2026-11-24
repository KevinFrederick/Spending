package com.kevinfreyap.jetspending.ui.action

import com.kevinfreyap.jetspending.ui.screen.privacy_security.state.PrivacySecurityDialogType

sealed interface PrivacySecurityAction {
    data class OnShowDialog(val dialog: PrivacySecurityDialogType): PrivacySecurityAction
    data class OnPasswordChange(val current: String, val new: String): PrivacySecurityAction
    data class OnResetEmailChange(val email: String): PrivacySecurityAction
    data object OnSubmitPassword: PrivacySecurityAction
    data object OnConfirmDelete: PrivacySecurityAction
    data object OnToggleAppLock: PrivacySecurityAction
    data object OnToggleBlockScreenshot: PrivacySecurityAction
    data object OnSendResetEmail: PrivacySecurityAction
}