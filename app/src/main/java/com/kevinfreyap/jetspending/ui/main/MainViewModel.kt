package com.kevinfreyap.jetspending.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.usecase.authentication.AuthenticationUseCase
import com.kevinfreyap.domain.usecase.category.CategoryUseCase
import com.kevinfreyap.domain.usecase.currency.CurrencyUseCase
import com.kevinfreyap.domain.usecase.rates.ExchangeRatesUseCase
import com.kevinfreyap.jetspending.ui.navigation.Screen
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val currencyUseCase: CurrencyUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
    private val exchangeRatesUseCase: ExchangeRatesUseCase,
    private val categoryUseCase: CategoryUseCase
): ViewModel(){
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    val selectedCurrency = currencyUseCase.getCurrency()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppCurrency.IDR
        )

    init {
        // viewModelScope.launch runs line by line
        viewModelScope.launch {
            exchangeRatesUseCase.startRatesHealer()
        }
        viewModelScope.launch {
            categoryUseCase.syncCategoriesFromFirestore()
        }
        viewModelScope.launch {
            val isLoggedIn = authenticationUseCase.isUserLoggedIn()
            _startDestination.value = if (isLoggedIn) Screen.Dashboard.route else Screen.OnBoarding.route
        }
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