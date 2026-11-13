package com.kevinfreyap.jetspending.ui.screen.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.authentication.AuthenticationUseCase
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.SignUpAction
import com.kevinfreyap.jetspending.ui.navigation.SignUpNavigation
import com.kevinfreyap.jetspending.ui.state.SignUpState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.utils.AuthUiValidation
import com.kevinfreyap.jetspending.utils.ErrorHelper
import com.kevinfreyap.jetspending.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase
): ViewModel() {
    private val _uiEvent = Channel<UiEvent<SignUpNavigation>>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    fun onAction(action: SignUpAction) {
        when(action) {
            is SignUpAction.OnEmailChanged -> onEmailChange(action.email)
            is SignUpAction.OnPasswordChanged -> onPassChange(action.password)
            is SignUpAction.OnConfirmPasswordChanged -> onConfirmPassChange(action.confirmPassword)
            is SignUpAction.OnGoogleAuthClicked -> onAuthWithGoogle(action.idToken)
            is SignUpAction.OnSignUpClicked -> onSignUpClicked()
        }
    }

    private fun onEmailChange(newEmail: String) {
        _state.update { currentState ->
            currentState.copy(
                email = newEmail,
                authStatus = ErrorHelper.removeError(currentState.authStatus, Field.AUTHENTICATION_EMAIL)
            )
        }
    }

    private fun onPassChange(newPass: String) {
        _state.update { currentState ->
            currentState.copy(
                password = newPass,
                authStatus = ErrorHelper.removeError(currentState.authStatus, Field.AUTHENTICATION_PASSWORD)
            )
        }
    }

    private fun onConfirmPassChange(newPass: String) {
        _state.update { currentState ->
            currentState.copy(
                confirmPassword = newPass,
                authStatus = ErrorHelper.removeError(currentState.authStatus, Field.AUTHENTICATION_CONFIRM_PASSWORD)
            )
        }
    }

    private fun onSignUpClicked() {
        if (_state.value.authStatus is UiState.Loading) return

        _state.update { currentState ->
            currentState.copy(
                authStatus = UiState.Loading
            )
        }

        val validationRes = AuthUiValidation.validateEmailAndPassword(
            email = _state.value.email,
            password = _state.value.password,
            confirmPassword = _state.value.confirmPassword
        )

        if (validationRes.isNotEmpty()) {
            _state.update { currentStatus ->
                currentStatus.copy(
                    authStatus = UiState.ValidationErrors(ErrorHelper.validationErrorsToUiError(validationRes))
                )
            }
            return
        }

        viewModelScope.launch {
            val result = authenticationUseCase.register(
                email = _state.value.email,
                password = _state.value.password,
                confirmPassword = _state.value.confirmPassword
            )

            when(result) {
                is DomainResult.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            authStatus = UiState.Success(Unit)
                        )
                    }

                    _uiEvent.send(UiEvent.ShowToast(R.string.success_register_success))
                    _uiEvent.send(UiEvent.Navigate(SignUpNavigation.NavigateToDashboard))

                    _state.value = SignUpState()
                }
                is DomainResult.ValidationFailed -> {
                    _state.update { currentState ->
                        currentState.copy(
                            authStatus = UiState.ValidationErrors(ErrorHelper.validationErrorsToUiError(result.errors))
                        )
                    }
                }
                is DomainResult.Failure -> {
                    Log.e(VIEW_MODEL_TAG, result.throwable.message ?: "Something Wrong")
                    _state.update { currentState ->
                        currentState.copy(
                            authStatus = UiState.Failure(result.throwable)
                        )
                    }
                }
            }
        }
    }

    private fun onAuthWithGoogle(idToken: String) {
        if (_state.value.authStatus is UiState.Loading) return

        _state.update { currentState ->
            currentState.copy(
                authStatus = UiState.Loading
            )
        }

        viewModelScope.launch {
            when(
                val result = authenticationUseCase.authWithGoogle(idToken)
            ) {
                is DomainResult.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            authStatus = UiState.Success(Unit)
                        )
                    }

                    _uiEvent.send(UiEvent.ShowToast(R.string.success_register_success))
                    _uiEvent.send(UiEvent.Navigate(SignUpNavigation.NavigateToDashboard))

                    _state.value = SignUpState()
                }
                is DomainResult.ValidationFailed -> {
                    _state.update { currentState ->
                        currentState.copy(
                            authStatus = UiState.ValidationErrors(ErrorHelper.validationErrorsToUiError(result.errors))
                        )
                    }
                }
                is DomainResult.Failure -> {
                    Log.e(VIEW_MODEL_TAG, result.throwable.message ?: "Something Wrong")
                    _state.update { currentState ->
                        currentState.copy(
                            authStatus = UiState.Failure(result.throwable)
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val VIEW_MODEL_TAG = "SignUpViewModel"
    }
}