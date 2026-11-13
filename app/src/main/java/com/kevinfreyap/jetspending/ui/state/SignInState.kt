package com.kevinfreyap.jetspending.ui.state

data class SignInState(
    val emailInput: String = "",
    val passwordInput: String = "",
    val resetPasswordEmailInput: String = "",
    val authStatus: UiState<Unit> = UiState.Idle
)
