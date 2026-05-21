package com.kevinfreyap.jetspending.ui.state

interface EditProfileAction {
    fun onUserNameChange(newUserName: String)
    fun onImageSelected(newImageUrl: String)
    fun dismissSuccessDialog()
    fun saveProfile()
}