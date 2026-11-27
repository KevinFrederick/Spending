package com.kevinfreyap.jetspending.ui.screen.privacy_security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.error.ErrorReporter
import com.kevinfreyap.domain.error.Field
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.authentication.ChangePasswordUseCase
import com.kevinfreyap.domain.usecase.authentication.CreatePasswordUseCase
import com.kevinfreyap.domain.usecase.authentication.DeleteAccountUseCase
import com.kevinfreyap.domain.usecase.authentication.ResetPasswordUseCase
import com.kevinfreyap.domain.usecase.privacy_security.GetAppLockPrefUseCase
import com.kevinfreyap.domain.usecase.privacy_security.GetBlockScreenPrefUseCase
import com.kevinfreyap.domain.usecase.privacy_security.SetAppLockPrefUseCase
import com.kevinfreyap.domain.usecase.privacy_security.SetBlockScreenshotPrefUseCase
import com.kevinfreyap.domain.usecase.user.GetUserUseCase
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.PrivacySecurityAction
import com.kevinfreyap.jetspending.ui.helper.error.removeError
import com.kevinfreyap.jetspending.ui.helper.error.toUIError
import com.kevinfreyap.jetspending.ui.navigation.PrivacySecurityNavigation
import com.kevinfreyap.jetspending.ui.screen.privacy_security.state.PasswordFormState
import com.kevinfreyap.jetspending.ui.screen.privacy_security.state.PrivacySecurityDialogType
import com.kevinfreyap.jetspending.ui.state.PrivacySecurityState
import com.kevinfreyap.jetspending.ui.state.UiEvent
import com.kevinfreyap.jetspending.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacySecurityViewModel @Inject constructor(
    getUser: GetUserUseCase,
    getAppLockPref: GetAppLockPrefUseCase,
    getBlockScreenPref: GetBlockScreenPrefUseCase,
    private val setAppLockPref: SetAppLockPrefUseCase,
    private val setBlockScreenPref: SetBlockScreenshotPrefUseCase,
    private val changePassword: ChangePasswordUseCase,
    private val createPassword: CreatePasswordUseCase,
    private val deleteAccount: DeleteAccountUseCase,
    private val resetPassword: ResetPasswordUseCase,
    private val errorReporter: ErrorReporter
): ViewModel() {
    private val _uiEvent = Channel<UiEvent<PrivacySecurityNavigation>>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _activeDialog = MutableStateFlow(PrivacySecurityDialogType.NONE)
    private val _passwordForm = MutableStateFlow(PasswordFormState())
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    private val _resetEmail = MutableStateFlow("")

    private val domainPrefsFlow = combine(
        getUser(),
        getAppLockPref(),
        getBlockScreenPref()
    ) { user, appLock, blockScreen ->
        Triple(user, appLock, blockScreen)
    }

    val state: StateFlow<PrivacySecurityState> = combine(
        domainPrefsFlow,
        _activeDialog,
        _passwordForm,
        _resetEmail,
        _uiState
    ) { (user, appLock, blockScreen), activeDialog, passwordForm, resetEmail, uiState ->
        PrivacySecurityState(
            isAppLockEnabled = appLock,
            isScreenshotBlockEnabled = blockScreen,
            hasPassword = user?.hasPassword ?: false,
            userEmail = user?.email ?: "",
            resetEmail = resetEmail,
            activeDialog = activeDialog,
            passwordForm = passwordForm,
            uiState = uiState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PrivacySecurityState()
    )

    fun onAction(action: PrivacySecurityAction) {
        when(action) {
            is PrivacySecurityAction.OnShowDialog -> {
                _activeDialog.value = action.dialog
                _passwordForm.value = PasswordFormState()
                if (action.dialog == PrivacySecurityDialogType.FORGOT_PASSWORD) {
                    val userEmail = state.value.userEmail
                    _resetEmail.value = userEmail
                } else {
                    _resetEmail.value = ""
                }
            }

            is PrivacySecurityAction.OnPasswordChange -> {
                _passwordForm.update { currentState ->
                    currentState.copy(
                        password = action.current,
                        newPassword = action.new
                    )
                }

                _uiState.value = _uiState.value.removeError(Field.AUTHENTICATION_PASSWORD)
                _uiState.value = _uiState.value.removeError(Field.AUTHENTICATION_NEW_PASSWORD)
            }
            is PrivacySecurityAction.OnResetEmailChange -> {
                _resetEmail.value = action.email
                _uiState.value = _uiState.value.removeError(Field.AUTHENTICATION_CHANGE_PASSWORD_EMAIL)
            }
            PrivacySecurityAction.OnToggleAppLock -> {
                viewModelScope.launch {
                    setAppLockPref(!state.value.isAppLockEnabled)
                }
            }
            PrivacySecurityAction.OnToggleBlockScreenshot -> {
                viewModelScope.launch {
                    setBlockScreenPref(!state.value.isScreenshotBlockEnabled)
                }
            }
            PrivacySecurityAction.OnSubmitPassword -> submitPassword()
            PrivacySecurityAction.OnConfirmDelete -> confirmDeleteAccount()
            PrivacySecurityAction.OnSendResetEmail -> sendResetEmail()
        }
    }

    private fun submitPassword() {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            val result = if (state.value.hasPassword) {
                changePassword(
                    oldPassword = _passwordForm.value.password,
                    newPassword = _passwordForm.value.newPassword
                )
            } else {
                createPassword(
                    newPassword = _passwordForm.value.newPassword
                )
            }
            when(result) {
                is DomainResult.Success -> {
                    _activeDialog.value = PrivacySecurityDialogType.NONE
                    _uiState.value = UiState.Success(Unit)

                    val toastRes = if (state.value.hasPassword) R.string.success_change_password else R.string.success_create_password
                    _uiEvent.send(UiEvent.ShowToast(toastRes))
                }
                is DomainResult.ValidationFailed -> {
                    _uiState.value = UiState.ValidationErrors(result.errors.toUIError())
                }
                is DomainResult.Failure -> {
                    errorReporter.recordException(result.throwable)
                    _uiState.value = UiState.Failure(result.throwable)

                    _uiEvent.send(
                        UiEvent.ShowToast(
                            messageRes = R.string.error_something_went_wrong
                        )
                    )
                }
            }
        }
    }

    private fun confirmDeleteAccount() {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            val passwordToVerify = if (state.value.hasPassword) state.value.passwordForm.password else null

            when(
                val result = deleteAccount(passwordToVerify)
            ) {
                is DomainResult.Success -> {
                    _uiState.value = UiState.Success(Unit)
                    _activeDialog.value = PrivacySecurityDialogType.NONE

                    _uiEvent.send(UiEvent.ShowToast(R.string.success_account_deleted))
                    _uiEvent.send(UiEvent.Navigate(PrivacySecurityNavigation.NavigateToOnboard))
                }
                is DomainResult.ValidationFailed -> {
                    _uiState.value = UiState.ValidationErrors(result.errors.toUIError())
                }
                is DomainResult.Failure -> {
                    errorReporter.recordException(result.throwable)
                    _uiState.value = UiState.Failure(result.throwable)

                    _uiEvent.send(
                        UiEvent.ShowToast(
                            messageRes = R.string.error_something_went_wrong
                        )
                    )
                }
            }
        }
    }

    private fun sendResetEmail() {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            when(
                val result = resetPassword(_resetEmail.value)
            ) {
                is DomainResult.Success -> {
                    _uiState.value = UiState.Success(Unit)
                    _activeDialog.value = PrivacySecurityDialogType.NONE

                    _uiEvent.send(UiEvent.ShowToast(R.string.success_email_sent))
                }
                is DomainResult.ValidationFailed -> {
                    _uiState.value = UiState.ValidationErrors(result.errors.toUIError())
                }
                is DomainResult.Failure -> {
                    errorReporter.recordException(result.throwable)
                    _uiState.value = UiState.Failure(result.throwable)

                    _uiEvent.send(
                        UiEvent.ShowToast(
                            messageRes = R.string.error_something_went_wrong
                        )
                    )
                }
            }
        }
    }
}