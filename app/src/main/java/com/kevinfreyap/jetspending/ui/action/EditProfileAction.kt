package com.kevinfreyap.jetspending.ui.action

sealed interface EditProfileAction {
    data class OnUsernameChange(val name: String): EditProfileAction
    data class OnShowDiscardDialog(val isVisible: Boolean): EditProfileAction
    data object OnConfirmDiscard: EditProfileAction
    data object OnSaveChanges: EditProfileAction
}