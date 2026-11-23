package com.kevinfreyap.jetspending.ui.state

data class EditProfileState(
    val userId: String = "",
    val userName: String = "",
    val email: String = "",
    val imageUrl: String? = null,
    val originalUsername: String = "",
    val isDiscardDialogVisible: Boolean = false,
    val uiState: UiState<Unit> = UiState.Idle
) {
    val hasChanges: Boolean
        get() = originalUsername != userName
}
