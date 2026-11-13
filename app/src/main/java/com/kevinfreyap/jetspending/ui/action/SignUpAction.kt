package com.kevinfreyap.jetspending.ui.action

sealed interface SignUpAction {
    data class OnEmailChanged(val email: String): SignUpAction
    data class OnPasswordChanged(val password: String): SignUpAction
    data class OnConfirmPasswordChanged(val confirmPassword: String): SignUpAction
    data class OnGoogleAuthClicked(val idToken: String): SignUpAction
    object OnSignUpClicked: SignUpAction
}