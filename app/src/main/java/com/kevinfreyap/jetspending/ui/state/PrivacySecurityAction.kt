package com.kevinfreyap.jetspending.ui.state

interface PrivacySecurityAction {
    fun onAppLockSwitch(isEnabled: Boolean)
    fun onBlockScreenSwitch(isEnabled: Boolean)
    fun onPasswordChange(pass: String)
    fun onNewPasswordChange(pass: String)
    fun onConfirmPasswordDialog()
    fun onCancelPasswordDialog()
}