package com.kevinfreyap.jetspending.ui.screen.privacy_security

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.authentication.AuthenticationUseCase
import com.kevinfreyap.domain.usecase.privacy_security.PrivacySecurityUseCase
import com.kevinfreyap.domain.usecase.user.UserUseCase
import com.kevinfreyap.jetspending.ui.state.PasswordFormState
import com.kevinfreyap.jetspending.ui.state.PrivacySecurityState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.utils.ErrorHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacySecurityViewModel @Inject constructor(
    private val privacySecurityUseCase: PrivacySecurityUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
    userUseCase: UserUseCase,
): ViewModel() {
    val privacySecurityState: StateFlow<PrivacySecurityState> = combine(
        flow = privacySecurityUseCase.getAppLockPref(),
        flow2 = privacySecurityUseCase.getBlockScreenPref(),
        flow3 = userUseCase.getUserProfile()
    ) { appLock, blockScreen, userProfile ->
        PrivacySecurityState(
            isAppLockEnabled = appLock,
            isBlockScreenshotEnabled = blockScreen,
            hasPassword = userProfile?.hasPassword ?: true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PrivacySecurityState()
    )

    private val _passwordForm = MutableStateFlow(PasswordFormState())
    val passwordForm = _passwordForm.asStateFlow()

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    fun toggleAppLock(isEnabled: Boolean) {
        viewModelScope.launch {
            privacySecurityUseCase.setAppLockPref(isEnabled)
        }
    }

    fun toggleBlockScreen(isEnabled: Boolean) {
        viewModelScope.launch {
            privacySecurityUseCase.setBlockScreenPref(isEnabled)
        }
    }

    fun onPasswordChange(password: String) {
        _passwordForm.update {
            it.copy(
                password = password
            )
        }

        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.AUTHENTICATION_PASSWORD)
    }

    fun onNewPasswordChange(newPassword: String) {
        _passwordForm.update {
            it.copy(
                newPassword = newPassword
            )
        }

        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.AUTHENTICATION_NEW_PASSWORD)
    }

    fun changePassword() {
        _uiState.value = UiState.Loading

        val oldPassword = _passwordForm.value.password
        val newPassword = _passwordForm.value.newPassword

        viewModelScope.launch {
            when(val result = authenticationUseCase.changePassword(oldPassword, newPassword)) {
                is DomainResult.Success -> {
                    _uiState.value = UiState.Success(Unit)
                    _showDialog.value = true
                }
                is DomainResult.ValidationFailed -> {
                    _uiState.value = UiState.ValidationErrors(
                        ErrorHelper.validationErrorsToUiError(result.errors)
                    )
                }
                is DomainResult.Failure -> {
                    Log.e(VIEW_MODEL_TAG, result.throwable.message ?: "Something Wrong")
                    _uiState.value = UiState.Failure(result.throwable)
                }
            }
        }
    }

    fun createPassword() {
        _uiState.value = UiState.Loading

        val newPassword = _passwordForm.value.newPassword

        viewModelScope.launch {
            when(val result = authenticationUseCase.createPassword(newPassword)) {
                is DomainResult.Success -> {
                    _uiState.value = UiState.Success(Unit)
                    _showDialog.value = true
                }
                is DomainResult.ValidationFailed -> {
                    _uiState.value = UiState.ValidationErrors(
                        ErrorHelper.validationErrorsToUiError(result.errors)
                    )
                }
                is DomainResult.Failure -> {
                    Log.e(VIEW_MODEL_TAG, result.throwable.message ?: "Something Wrong")
                    _uiState.value = UiState.Failure(result.throwable)
                }
            }
        }
    }

    fun clearForm() {
        _passwordForm.value = PasswordFormState()
        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.AUTHENTICATION_PASSWORD)
        _uiState.value = ErrorHelper.removeError(_uiState.value, Field.AUTHENTICATION_NEW_PASSWORD)
    }

    fun onDismissDialog() {
        _showDialog.value = false
    }

    companion object {
        private const val VIEW_MODEL_TAG = "PrivacySecurityViewModel"
    }
}