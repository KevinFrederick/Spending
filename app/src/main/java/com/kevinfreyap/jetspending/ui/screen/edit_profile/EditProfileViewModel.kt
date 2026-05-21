package com.kevinfreyap.jetspending.ui.screen.edit_profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.user.UserUseCase
import com.kevinfreyap.jetspending.ui.state.EditProfileState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.utils.ErrorHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor (
    private val userUseCase: UserUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    // UiState
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    // Success
    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog = _showSuccessDialog.asStateFlow()

    // Failure
    private val _showFailureDialog = MutableStateFlow(false)
    val showFailureDialog = _showFailureDialog.asStateFlow()

    init {
        loadUserData()
    }

    fun onUsernameChange(newUsername: String) {
        _state.update { it.copy(userName = newUsername) }
        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.USERNAME)
    }

    fun onImageSelected(newImageUrl: String) {
        _state.update { it.copy(imageUrl = newImageUrl) }
    }

    fun saveProfile() {
        _uiState.value = UiState.Loading

        val currentUserId = _state.value.userId

        if (currentUserId.isBlank()) return

        viewModelScope.launch {
            val result = userUseCase.updateUserProfile(
                userId = _state.value.userId,
                newUsername = _state.value.userName,
                newImageUrl = _state.value.imageUrl
            )

            when(result) {
                is DomainResult.Success -> {
                    _uiState.value = UiState.Success(Unit)
                    _showSuccessDialog.value = true
                }
                is DomainResult.ValidationFailed -> {
                    _uiState.value = UiState.ValidationErrors(ErrorHelper.validationErrorsToUiError(result.errors))
                }
                is DomainResult.Failure -> {
                    Log.e(VIEWMODEL_TAG, result.throwable.message ?: "Something Wrong")
                    _uiState.value = UiState.Failure(result.throwable)
                }
            }
        }
    }

    fun onDismissSuccessDialog() {
        _showSuccessDialog.value = false
    }

    private fun loadUserData() {
        viewModelScope.launch {
            userUseCase.getUserProfile().collect { currentUser ->
                if (currentUser != null) {
                    _state.update { it.copy(
                        userId = currentUser.uid,
                        userName = currentUser.displayName,
                        email = currentUser.email,
                        imageUrl = currentUser.photoUrl,
                        originalUsername = currentUser.displayName,
                        originalImageUrl = currentUser.photoUrl
                    ) }
                }
            }
        }
    }

    companion object {
        private const val VIEWMODEL_TAG = "EditProfileViewModel"
    }
}