package com.kevinfreyap.jetspending.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.usecase.authentication.AuthenticationUseCase
import com.kevinfreyap.domain.usecase.category.CategoryUseCase
import com.kevinfreyap.domain.usecase.currency.CurrencyUseCase
import com.kevinfreyap.domain.usecase.rates.ExchangeRatesUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.ui.navigation.Screen
import com.kevinfreyap.domain.usecase.connectivity.ConnectivityUseCase
import com.kevinfreyap.domain.usecase.user.UserUseCase
import com.kevinfreyap.jetspending.ui.state.MainActivityState
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val currencyUseCase: CurrencyUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
    private val exchangeRatesUseCase: ExchangeRatesUseCase,
    private val transactionUseCase: TransactionUseCase,
    private val categoryUseCase: CategoryUseCase,
    userUseCase: UserUseCase,
    connectivityUseCase: ConnectivityUseCase
): ViewModel(){
    val isOnline = connectivityUseCase.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val selectedCurrency = currencyUseCase.getCurrency()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppCurrency.IDR)

    private val isLoggedInFlow = flow {
        emit(authenticationUseCase.isUserLoggedIn())
    }

    val uiState: StateFlow<MainActivityState> = combine(
        userUseCase.getCurrentTheme(),
        isLoggedInFlow,
    ) { theme, isLoggedIn ->
        val startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.OnBoarding.route

        MainActivityState.Success(
            theme = theme,
            startDestination = startDestination,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainActivityState.Loading
    )

    init {
        // viewModelScope.launch runs line by line
        startBackgroundSync()
        observeAuthState()
    }

    fun startBackgroundSync() {
        viewModelScope.launch(Dispatchers.IO) {
            launch { exchangeRatesUseCase.startRatesHealer() }
            launch { exchangeRatesUseCase.syncDailyRates() }
            launch { categoryUseCase.syncCategoriesFromFirestore() }
        }
    }

    fun observeAuthState() {
        authenticationUseCase.getAuthState()
            .flatMapLatest { isLoggedIn ->
                if (isLoggedIn) {
                    transactionUseCase.syncTransactionsFromFirestore()
                } else {
                    emptyFlow()
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSelectCurrency(newCurrency: AppCurrency) {
        viewModelScope.launch {
            currencyUseCase.setCurrency(newCurrency)
        }
    }

    fun onRateMissing(date: Instant) {
        val dateString = DateFormatter.formatToDailyRatesString(date)
        viewModelScope.launch(Dispatchers.IO) {
            exchangeRatesUseCase.ensureRatesExist(dateString)
        }
    }
}