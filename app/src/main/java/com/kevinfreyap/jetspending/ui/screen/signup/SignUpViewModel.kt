package com.kevinfreyap.jetspending.ui.screen.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.error.ErrorReporter
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.authentication.AuthWithGoogleUseCase
import com.kevinfreyap.domain.usecase.authentication.SignUpUseCase
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.SignUpAction
import com.kevinfreyap.jetspending.ui.helper.error.removeError
import com.kevinfreyap.jetspending.ui.helper.error.toUIError
import com.kevinfreyap.jetspending.ui.navigation.SignUpNavigation
import com.kevinfreyap.jetspending.ui.state.SignUpState
import com.kevinfreyap.jetspending.ui.state.UiEvent
import com.kevinfreyap.jetspending.ui.state.UiState
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
    private val signUpUseCase: SignUpUseCase,
    private val authWithGoogleUseCase: AuthWithGoogleUseCase,
    private val errorReporter: ErrorReporter
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
                authStatus = currentState.authStatus.removeError(Field.AUTHENTICATION_EMAIL)
            )
        }
    }

    private fun onPassChange(newPass: String) {
        _state.update { currentState ->
            currentState.copy(
                password = newPass,
                authStatus = currentState.authStatus.removeError(Field.AUTHENTICATION_PASSWORD)
            )
        }
    }

    private fun onConfirmPassChange(newPass: String) {
        _state.update { currentState ->
            currentState.copy(
                confirmPassword = newPass,
                authStatus = currentState.authStatus.removeError(Field.AUTHENTICATION_CONFIRM_PASSWORD)
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

        viewModelScope.launch {
            val result = signUpUseCase(
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
                }
                is DomainResult.ValidationFailed -> {
                    _state.update { currentState ->
                        currentState.copy(
                            authStatus = UiState.ValidationErrors(result.errors.toUIError())
                        )
                    }
                }
                is DomainResult.Failure -> {
                    errorReporter.recordException(result.throwable)
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
                val result = authWithGoogleUseCase(idToken)
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
                            authStatus = UiState.ValidationErrors(result.errors.toUIError())
                        )
                    }
                }
                is DomainResult.Failure -> {
                    errorReporter.recordException(result.throwable)
                    _state.update { currentState ->
                        currentState.copy(
                            authStatus = UiState.Failure(result.throwable)
                        )
                    }
                }
            }
        }
    }
}