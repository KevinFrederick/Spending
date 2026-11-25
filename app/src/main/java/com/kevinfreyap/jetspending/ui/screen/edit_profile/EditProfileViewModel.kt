package com.kevinfreyap.jetspending.ui.screen.edit_profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.user.GetUserUseCase
import com.kevinfreyap.domain.usecase.user.UpdateUserUseCase
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.EditProfileAction
import com.kevinfreyap.jetspending.ui.state.EditProfileState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.helper.error.removeError
import com.kevinfreyap.jetspending.ui.helper.error.toUIError
import com.kevinfreyap.jetspending.ui.navigation.EditProfileNavigation
import com.kevinfreyap.jetspending.ui.state.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor (
    private val getUser: GetUserUseCase,
    private val updateUser: UpdateUserUseCase
): ViewModel() {
    private val _uiEvent = Channel<UiEvent<EditProfileNavigation>>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    init {
        loadInitialData()
    }

    fun onAction(action: EditProfileAction) {
        when(action) {
            is EditProfileAction.OnShowDiscardDialog -> {
                _state.update { currentState ->
                    currentState.copy(
                        isDiscardDialogVisible = action.isVisible
                    )
                }
            }
            is EditProfileAction.OnUsernameChange -> {
                _state.update { currentState ->
                    currentState.copy(
                        userName = action.name,
                        uiState = currentState.uiState.removeError(Field.USERNAME)
                    )
                }
            }
            EditProfileAction.OnConfirmDiscard -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(EditProfileNavigation.NavigateUp))
                }
            }
            EditProfileAction.OnSaveChanges -> onSaveChanges()
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val user = getUser().firstOrNull()

            if (user != null) {
                _state.update { currentState ->
                    currentState.copy(
                        userName = user.displayName,
                        originalUsername = user.displayName,
                        email = user.email,
                        imageUrl = user.photoUrl
                    )
                }
            } else {
                _uiEvent.send(UiEvent.ShowToast(R.string.error_user_not_found))
                _uiEvent.send(UiEvent.Navigate(EditProfileNavigation.NavigateUp))
            }
        }
    }

    private fun onSaveChanges() {
        _state.update { currentState ->
            currentState.copy(
                uiState = UiState.Loading
            )
        }

        viewModelScope.launch {
            when(
                val result = updateUser(
                    userName = _state.value.userName
                )
            ) {
                is DomainResult.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            uiState = UiState.Success(Unit)
                        )
                    }

                    _uiEvent.send(UiEvent.ShowToast(R.string.success_profile_update))
                    _uiEvent.send(UiEvent.Navigate(EditProfileNavigation.NavigateUp))
                }
                is DomainResult.ValidationFailed -> {
                    _state.update { currentState ->
                        currentState.copy(
                            uiState = UiState.ValidationErrors(result.errors.toUIError())
                        )
                    }
                }
                is DomainResult.Failure -> {
                    Log.e(VIEWMODEL_TAG, result.throwable.message ?: "Something went wrong")
                    _state.update { currentState ->
                        currentState.copy(
                            uiState = UiState.Failure(result.throwable)
                        )
                    }

                    _uiEvent.send(
                        UiEvent.ShowToast(
                            messageRes = R.string.error_something_went_wrong
                        )
                    )
                }
            }
        }
    }

    companion object {
        private const val VIEWMODEL_TAG = "EditProfileViewModel"
    }
}