package com.kevinfreyap.jetspending.ui.state

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val authStatus: UiState<Unit> = UiState.Idle
)
