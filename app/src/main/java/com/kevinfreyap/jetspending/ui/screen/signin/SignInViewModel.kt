package com.kevinfreyap.jetspending.ui.screen.signin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.authentication.AuthenticationUseCase
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.SignInAction
import com.kevinfreyap.jetspending.ui.navigation.SignInNavigation
import com.kevinfreyap.jetspending.ui.state.SignInState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.utils.AuthUiValidation
import com.kevinfreyap.jetspending.ui.helper.error.removeError
import com.kevinfreyap.jetspending.ui.helper.error.toUIError
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
class SignInViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase
): ViewModel(){
    private val _uiEvent = Channel<UiEvent<SignInNavigation>>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onAction(action: SignInAction) {
        when(action) {
            is SignInAction.OnEmailChanged -> onEmailChange(action.email)
            is SignInAction.OnPasswordChanged -> onPassChange(action.password)
            is SignInAction.OnResetPasswordEmailChanged -> onResetEmailChange(action.resetEmail)
            is SignInAction.OnGoogleAuthClicked -> onAuthWithGoogle(action.idToken)
            is SignInAction.OnSignInClicked -> onSignInClicked()
            is SignInAction.OnSendResetEmail -> onSendResetEmail()
        }
    }

    private fun onEmailChange(newEmail: String) {
        _state.update { currentState ->
            currentState.copy(
                emailInput = newEmail,
                authStatus = currentState.authStatus.removeError(Field.AUTHENTICATION_EMAIL)
            )
        }
    }

    private fun onPassChange(newPass: String) {
        _state.update { currentState ->
            currentState.copy(
                passwordInput = newPass,
                authStatus = currentState.authStatus.removeError(Field.AUTHENTICATION_PASSWORD)
            )
        }
    }

    private fun onResetEmailChange(email: String) {
        _state.update { currentState ->
            currentState.copy(
                resetPasswordEmailInput = email,
                authStatus = currentState.authStatus.removeError(Field.AUTHENTICATION_CHANGE_PASSWORD_EMAIL)
            )
        }
    }

    private fun onSignInClicked() {
        if (_state.value.authStatus is UiState.Loading) return

        _state.update { currentState ->
            currentState.copy(
                authStatus = UiState.Loading
            )
        }

        val validationRes = AuthUiValidation.validateEmailAndPassword(
            email = _state.value.emailInput,
            password = _state.value.passwordInput
        )

        if (validationRes.isNotEmpty()) {
            _state.update { currentStatus ->
                currentStatus.copy(
                    authStatus = UiState.ValidationErrors(validationRes.toUIError())
                )
            }
            return
        }

        viewModelScope.launch {
            val result = authenticationUseCase.login(
                email = _state.value.emailInput,
                password = _state.value.passwordInput
            )

            when(result) {
                is DomainResult.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            authStatus = UiState.Success(Unit)
                        )
                    }

                    _uiEvent.send(UiEvent.ShowToast(R.string.success_login_success))
                    _uiEvent.send(UiEvent.Navigate(SignInNavigation.NavigateToDashboard))

                    _state.value = SignInState()
                }
                is DomainResult.ValidationFailed -> {
                    _state.update { currentState ->
                        currentState.copy(
                            authStatus = UiState.ValidationErrors(result.errors.toUIError())
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

                    _uiEvent.send(UiEvent.ShowToast(R.string.success_login_success))
                    _uiEvent.send(UiEvent.Navigate(SignInNavigation.NavigateToDashboard))

                    _state.value = SignInState()
                }
                is DomainResult.ValidationFailed -> {
                    _state.update { currentState ->
                        currentState.copy(
                            authStatus = UiState.ValidationErrors(result.errors.toUIError())
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

    private fun onSendResetEmail() {
        val validationRes = AuthUiValidation.validateEmail(
            email = _state.value.resetPasswordEmailInput
        )

        if (validationRes.isNotEmpty()) {
            _state.update { currentState ->
                currentState.copy(
                    authStatus = UiState.ValidationErrors(validationRes.toUIError())
                )
            }
            return
        }

        viewModelScope.launch {
            when(
                val result = authenticationUseCase.resetPassword(_state.value.resetPasswordEmailInput)
            ) {
                is DomainResult.Success -> {
                    _state.update { currentState ->
                        currentState.copy(
                            authStatus = UiState.Success(Unit)
                        )
                    }

                    _uiEvent.send(UiEvent.ShowToast(R.string.success_email_sent))
                }
                is DomainResult.ValidationFailed -> {
                    _state.update { currentState ->
                        currentState.copy(
                            authStatus = UiState.ValidationErrors(result.errors.toUIError())
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
        private const val VIEW_MODEL_TAG = "SignInViewModel"
    }
}