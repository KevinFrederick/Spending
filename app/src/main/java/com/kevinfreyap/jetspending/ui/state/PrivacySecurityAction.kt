package com.kevinfreyap.jetspending.ui.state

interface PrivacySecurityAction {
    fun onAppLockSwitch(isEnabled: Boolean)
    fun onBlockScreenSwitch(isEnabled: Boolean)
}