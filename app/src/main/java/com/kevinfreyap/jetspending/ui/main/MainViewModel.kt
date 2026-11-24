package com.kevinfreyap.jetspending.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.usecase.authentication.GetAuthStatusUseCase
import com.kevinfreyap.domain.usecase.authentication.LogoutUseCase
import com.kevinfreyap.jetspending.ui.navigation.Screen
import com.kevinfreyap.domain.usecase.connectivity.ConnectivityUseCase
import com.kevinfreyap.domain.usecase.currency.GetCurrencyUseCase
import com.kevinfreyap.domain.usecase.privacy_security.GetAppLockPrefUseCase
import com.kevinfreyap.domain.usecase.privacy_security.GetBlockScreenPrefUseCase
import com.kevinfreyap.domain.usecase.privacy_security.SetAppLockPrefUseCase
import com.kevinfreyap.domain.usecase.rates.EnsureRatesExistUseCase
import com.kevinfreyap.domain.usecase.rates.RatesHealerUseCase
import com.kevinfreyap.domain.usecase.sync.SyncCategoriesUseCase
import com.kevinfreyap.domain.usecase.sync.SyncRatesUseCase
import com.kevinfreyap.domain.usecase.sync.SyncTransactionsUseCase
import com.kevinfreyap.domain.usecase.user.GetThemeUseCase
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getTheme: GetThemeUseCase,
    getAppLockPreference: GetAppLockPrefUseCase,
    getBlockScreenPreference: GetBlockScreenPrefUseCase,
    getCurrency: GetCurrencyUseCase,
    checkConnection: ConnectivityUseCase,
    private val logout: LogoutUseCase,
    private val syncTransactions: SyncTransactionsUseCase,
    private val syncCategories: SyncCategoriesUseCase,
    private val syncRates: SyncRatesUseCase,
    private val ratesHealer: RatesHealerUseCase,
    private val ensureRatesExist: EnsureRatesExistUseCase,
    private val setAppLockPref: SetAppLockPrefUseCase,
    private val getAuthStatus: GetAuthStatusUseCase,
): ViewModel(){

    private data class SecurityAndAuth(
        val isLoggedIn: Boolean,
        val isAppLockEnabled: Boolean,
        val isSecureScreenEnabled: Boolean,
        val isUnlocked: Boolean
    )

    private val _isSessionUnlocked = MutableStateFlow(false)

    private val _effects = Channel<MainEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<MainActivityState> = combine(
        getTheme(),
        getCurrency(),
        checkConnection(),
        combine(
            getAuthStatus(),
            getAppLockPreference(),
            getBlockScreenPreference(),
            _isSessionUnlocked,
            ::SecurityAndAuth
        )
    ) { theme, selectedCurrency, isOnline, security ->
        val startDestination = if (security.isLoggedIn) Screen.Dashboard.route else Screen.OnBoarding.route

        val effectiveUnlocked = !security.isLoggedIn || !security.isAppLockEnabled || security.isUnlocked

        MainActivityState.Success(
            theme = theme,
            startDestination = startDestination,
            isAppLockEnabled = security.isAppLockEnabled,
            isSecureScreenEnabled = security.isSecureScreenEnabled,
            isUnlocked = effectiveUnlocked,
            selectedCurrency = selectedCurrency,
            isOnline = isOnline
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainActivityState.Loading
    )

    init {
        observeAuthAndSync()
    }

    fun onEvent(event: MainEvent) {
        Log.d("AppLock", "Event received: $event")
        when(event) {
            is MainEvent.RateMissing -> {
                val dateString = DateFormatter.formatToDailyRatesString(event.date)
                viewModelScope.launch(Dispatchers.IO) {
                    ensureRatesExist(dateString)
                }
            }
            is MainEvent.SetUnlocked -> {
                _isSessionUnlocked.value = event.unlocked
            }
            MainEvent.AppBackgrounded -> {
                if ((uiState.value as? MainActivityState.Success)?.isAppLockEnabled == true) {
                    _isSessionUnlocked.value = false
                }
            }
            MainEvent.EmergencyLogout -> {
                performEmergencyLogout()
            }

            MainEvent.DisableAppLockAndUnlock -> {
                viewModelScope.launch {
                    setAppLockPref(false)
                    _isSessionUnlocked.value = true
                }
            }
        }
    }

    private fun performEmergencyLogout() {
        viewModelScope.launch {
            setAppLockPref(false)
            logout()
            _effects.send(MainEffect.NavigateToOnBoarding)
        }
    }

    private fun observeAuthAndSync() {
        viewModelScope.launch {
            getAuthStatus()
                .distinctUntilChanged()
                .collectLatest { isLoggedIn ->
                    if (isLoggedIn) {
                        coroutineScope {
                            launch { syncCategories() }
                            launch { syncRates() }
                            launch { ratesHealer() }
                            launch { syncTransactions().collect() }
                        }
                    }
                }
        }
    }
}