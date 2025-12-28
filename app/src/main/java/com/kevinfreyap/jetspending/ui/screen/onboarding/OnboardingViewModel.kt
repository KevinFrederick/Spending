package com.kevinfreyap.jetspending.ui.screen.onboarding

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.authentication.AuthenticationUseCase
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.utils.ErrorHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

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

    fun onDismissDialog() {
        _showDialog.value = false
    }

    companion object {
        private const val VIEW_MODEL_TAG = "OnboardingViewModel"
    }
}