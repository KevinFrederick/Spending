package com.kevinfreyap.jetspending.ui.screen.signin

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.authentication.AuthenticationUseCase
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.utils.AuthUiValidation
import com.kevinfreyap.jetspending.utils.ErrorHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase
): ViewModel(){
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _resetEmail = MutableStateFlow("")
    val resetEmail: StateFlow<String> = _resetEmail

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    private val _showEmailSentDialog = MutableStateFlow(false)
    val showEmailSentDialog = _showEmailSentDialog.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail

        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.AUTHENTICATION_EMAIL)
    }

    fun onPassChange(newPass: String) {
        _password.value = newPass

        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.AUTHENTICATION_PASSWORD)
    }

    fun onResetEmailChange(email: String) {
        _resetEmail.value = email

        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.AUTHENTICATION_CHANGE_PASSWORD_EMAIL)
    }

    fun onSignInClicked() {
        _uiState.value = UiState.Loading

        val validationRes = AuthUiValidation.validateEmailAndPassword(
            email = _email.value,
            password = _password.value
        )

        if (validationRes.isNotEmpty()) {
            _uiState.value = UiState.ValidationErrors(ErrorHelper.validationErrorsToUiError(validationRes))
            return
        }

        viewModelScope.launch {
            val result = authenticationUseCase.login(
                email = _email.value,
                password = _password.value
            )

            when(result) {
                is DomainResult.Success -> {
                    _uiState.value = UiState.Success(Unit)
                    _showDialog.value = true
                }
                is DomainResult.ValidationFailed -> {
                    _uiState.value = UiState.ValidationErrors(ErrorHelper.validationErrorsToUiError(result.errors))
                }
                is DomainResult.Failure -> {
                    Log.e(VIEW_MODEL_TAG, result.throwable.message ?: "Something Wrong")
                    _uiState.value = UiState.Failure(result.throwable)
                }
            }
        }
    }

    fun onAuthWithGoogle(activity: Activity) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val result = authenticationUseCase.authWithGoogle(activity)

            when(result) {
                is DomainResult.Success -> {
                    _uiState.value = UiState.Success(Unit)
                    _showDialog.value = true
                }
                is DomainResult.ValidationFailed -> {
                    _uiState.value = UiState.ValidationErrors(ErrorHelper.validationErrorsToUiError(result.errors))
                }
                is DomainResult.Failure -> {
                    Log.e(VIEW_MODEL_TAG, result.throwable.message ?: "Something Wrong")
                    _uiState.value = UiState.Failure(result.throwable)
                }
            }
        }
    }

    fun onSendResetEmail() {
        val validationRes = AuthUiValidation.validateEmail(_resetEmail.value)

        if (validationRes.isNotEmpty()) {
            _uiState.value = UiState.ValidationErrors(ErrorHelper.validationErrorsToUiError(validationRes))
            return
        }

        viewModelScope.launch {
            val result = authenticationUseCase.resetPassword(_resetEmail.value)

            when(result) {
                is DomainResult.Success -> {
                    _uiState.value = UiState.Success(Unit)
                    _showEmailSentDialog.value = true
                }
                is DomainResult.ValidationFailed -> {
                    _uiState.value = UiState.ValidationErrors(ErrorHelper.validationErrorsToUiError(result.errors))
                }
                is DomainResult.Failure -> {
                    Log.e(VIEW_MODEL_TAG, result.throwable.message ?: "Something Wrong")
                    _uiState.value = UiState.Failure(result.throwable)
                }
            }
        }

    }

    fun onDismissDialog() {
        _showDialog.value = false
    }

    fun onDismissEmailSentDialog() {
        _showEmailSentDialog.value = false
    }

    companion object {
        private const val VIEW_MODEL_TAG = "SignInViewModel"
    }
}