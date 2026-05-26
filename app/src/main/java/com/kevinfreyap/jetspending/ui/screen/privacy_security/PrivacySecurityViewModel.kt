package com.kevinfreyap.jetspending.ui.screen.privacy_security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.usecase.privacy_security.PrivacySecurityUseCase
import com.kevinfreyap.jetspending.ui.state.PrivacySecurityState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivacySecurityViewModel @Inject constructor(
    private val privacySecurityUseCase: PrivacySecurityUseCase
): ViewModel() {

    val privacySecurityState: StateFlow<PrivacySecurityState> = combine(
        flow = privacySecurityUseCase.getAppLockPref(),
        flow2 = privacySecurityUseCase.getBlockScreenPref()
    ) { appLock, blockScreen ->
        PrivacySecurityState(
            isAppLockEnabled = appLock,
            isBlockScreenshotEnabled = blockScreen
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PrivacySecurityState()
    )

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
}