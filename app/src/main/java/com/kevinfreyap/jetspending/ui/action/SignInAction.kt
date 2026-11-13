package com.kevinfreyap.jetspending.ui.action

sealed interface SignInAction {
    data class OnEmailChanged(val email: String): SignInAction
    data class OnPasswordChanged(val password: String): SignInAction
    data class OnResetPasswordEmailChanged(val resetEmail: String): SignInAction
    data class OnGoogleAuthClicked(val idToken: String): SignInAction
    object OnSendResetEmail: SignInAction
    object OnSignInClicked: SignInAction
}
