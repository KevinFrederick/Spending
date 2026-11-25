package com.kevinfreyap.jetspending.ui.screen.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.usecase.authentication.LogoutUseCase
import com.kevinfreyap.domain.usecase.currency.GetCurrencyUseCase
import com.kevinfreyap.domain.usecase.currency.SetCurrencyUseCase
import com.kevinfreyap.domain.usecase.user.GetThemeUseCase
import com.kevinfreyap.domain.usecase.user.GetUserUseCase
import com.kevinfreyap.domain.usecase.user.SetThemeUseCase
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.SettingsAction
import com.kevinfreyap.jetspending.ui.navigation.SettingsNavigation
import com.kevinfreyap.jetspending.ui.screen.settings.state.ProfileCardState
import com.kevinfreyap.jetspending.ui.screen.settings.state.SettingsDialogType
import com.kevinfreyap.jetspending.ui.state.SettingsState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getUser: GetUserUseCase,
    getCurrency: GetCurrencyUseCase,
    getTheme: GetThemeUseCase,
    private val setCurrency: SetCurrencyUseCase,
    private val setTheme: SetThemeUseCase,
    private val logout: LogoutUseCase
): ViewModel() {
    private val _uiEvent = Channel<UiEvent<SettingsNavigation>>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _dialogState = MutableStateFlow(SettingsDialogType.NONE)

    val state: StateFlow<SettingsState> = combine(
        getUser(),
        getCurrency(),
        getTheme(),
        _dialogState
    ) {user, currency, theme, activeDialog ->
        if (user == null) {
            SettingsState(
                profile = ProfileCardState(),
                selectedCurrency = currency,
                selectedAppearance = theme,
                activeDialog = activeDialog,
                uiState = UiState.Loading
            )
        } else {
            val profile = ProfileCardState(
                imageUrl = user.photoUrl,
                name = user.displayName.ifBlank { "User" },
                email = user.email
            )

            SettingsState(
                profile = profile,
                selectedCurrency = currency,
                selectedAppearance = theme,
                activeDialog = activeDialog,
                uiState = UiState.Success(Unit)
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsState(
            uiState = UiState.Loading
        )
    )

    fun onAction(action: SettingsAction) {
        when(action) {
            is SettingsAction.OnSelectCurrency -> {
                _dialogState.value = SettingsDialogType.NONE

                viewModelScope.launch {
                    setCurrency(action.currency)
                }
            }
            is SettingsAction.OnSelectTheme -> {
                _dialogState.value = SettingsDialogType.NONE

                viewModelScope.launch {
                    setTheme(action.theme)
                }
            }
            is SettingsAction.OnShowDialog -> {
                _dialogState.value = action.dialog
            }

            SettingsAction.OnLogout -> {
                _dialogState.value = SettingsDialogType.NONE

                onLogout()
            }
        }
    }

    private fun onLogout() {
        viewModelScope.launch {
            try {
                logout()

                _uiEvent.send(UiEvent.ShowToast(R.string.success_logout))
                _uiEvent.send(UiEvent.Navigate(SettingsNavigation.NavigateToOnBoard))
            } catch (e: Exception) {
                Log.e(VIEW_MODEL_TAG, "Logout failed", e)
            }
        }
    }

    companion object {
        private const val VIEW_MODEL_TAG = "SettingsViewModel"
    }
}