package com.kevinfreyap.jetspending.ui.screen.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.authentication.AuthenticationUseCase
import com.kevinfreyap.domain.usecase.user.UserUseCase
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.model.SettingsGroup
import com.kevinfreyap.jetspending.ui.model.SettingsItem
import com.kevinfreyap.jetspending.ui.model.SettingsOption
import com.kevinfreyap.jetspending.ui.model.UserProfileUi
import com.kevinfreyap.jetspending.ui.theme.Red500
import com.kevinfreyap.jetspending.utils.mapper.UserProfileUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
    private val userProfileUiMapper: UserProfileUiMapper
): ViewModel() {
    val userProfile = userUseCase.getUserProfile()
        .map {
            if (it != null) {
                userProfileUiMapper.mapUserProfileDomainToUi(it)
            } else {
                UserProfileUi(
                    imageUrl = "",
                    displayName = "",
                    displayEmail = ""
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = UserProfileUi(
                imageUrl = "",
                displayName = "",
                displayEmail = ""
            )
        )

    private val _selectedCurrency = MutableStateFlow(R.string.currency_idr)
    private val _selectedTheme = MutableStateFlow(R.string.light_mode)

    val settingsState = combine(
        _selectedCurrency,
        _selectedTheme
    ) { currency, theme ->
        val generalList = listOf(
            SettingsItem(
                id = SettingsOption.EDIT_PROFILE,
                title = R.string.edit_profile,
                icon = R.drawable.ic_mode_edit_24,
            ),
            SettingsItem(
                id = SettingsOption.NOTIFICATION,
                title = R.string.notifications,
                icon = R.drawable.ic_notifications_24,
            ),
            SettingsItem(
                id = SettingsOption.PRIVACY_SECURITY,
                title = R.string.security,
                icon = R.drawable.ic_lock_24,
            ),
        )

        val preferencesList = listOf(
            SettingsItem(
                id = SettingsOption.CURRENCY,
                title = R.string.currency,
                icon = R.drawable.ic_currency_exchange_24,
                subtitle = currency
            ),
            SettingsItem(
                id = SettingsOption.THEME,
                title = R.string.theme,
                icon = R.drawable.ic_light_mode_24,
                subtitle = theme
            ),
        )

        val supportList = listOf(
            SettingsItem(
                id = SettingsOption.LOG_OUT,
                title = R.string.logout,
                icon = R.drawable.ic_logout_24,
                contentColor = Red500,
                showChevron = false
            ),
        )

        listOf(
            SettingsGroup(R.string.general, generalList),
            SettingsGroup(R.string.preference, preferencesList),
            SettingsGroup(R.string.support, supportList),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _navigationChannel = Channel<Unit>()
    val navigationChannel = _navigationChannel.receiveAsFlow()

    fun logout() {
        viewModelScope.launch {
            val result = authenticationUseCase.logout()

            if (result is DomainResult.Success) {
                _navigationChannel.send(Unit)
            } else {
                Log.e(VIEW_MODEL_TAG, (result as DomainResult.Failure).throwable.message ?: "Something Wrong")
            }
        }
    }

    companion object {
        private const val VIEW_MODEL_TAG = "SettingsViewModel"
    }
}