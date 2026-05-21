package com.kevinfreyap.jetspending.ui.state

data class EditProfileState(
    val userId: String = "",
    val userName: String = "",
    val email: String = "",
    val imageUrl: String? = null,
    val originalUsername: String = "",
    val originalImageUrl: String? = null
) {
    val hasChanges: Boolean
        get() = originalUsername != userName || originalImageUrl != imageUrl
}
