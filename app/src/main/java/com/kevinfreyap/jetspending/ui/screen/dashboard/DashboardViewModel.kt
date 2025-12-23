package com.kevinfreyap.jetspending.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.usecase.currency.CurrencyUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.ui.model.MonthlyBalanceUi
import com.kevinfreyap.jetspending.ui.model.TotalBalanceUi
import com.kevinfreyap.jetspending.utils.formatter.CurrencyUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import com.kevinfreyap.jetspending.utils.mapper.TransactionItemUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    transactionUseCase: TransactionUseCase,
    currencyUseCase: CurrencyUseCase,
    private val transactionItemUiMapper: TransactionItemUiMapper
): ViewModel() {
    val currencyCode = currencyUseCase.getCurrency()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppCurrency.IDR
        )

    val latestTransactions = combine(
        transactionUseCase.getLatestTransactions(),
        currencyCode
    ) { transactions, currency ->
        transactions.map { transaction ->
            transactionItemUiMapper.mapTransactionDomainToUi(transaction, currency)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _currentMonth = MutableStateFlow(YearMonth.now())

    val nextBtnEnabled: StateFlow<Boolean> = _currentMonth.map { month ->
        val now = YearMonth.now()
        month < now
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val previousBtnEnabled: StateFlow<Boolean> = _currentMonth.map { month ->
        val minYear = YearMonth.now()
            .minusYears(2)
            .withMonth(1)
        month > minYear
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val monthDisplay = _currentMonth.map {
        DateFormatter.formatYearMonthToString(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DateFormatter.formatYearMonthToString(YearMonth.now())
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalBalance = currencyCode
        .flatMapLatest {
            transactionUseCase.getTotalBalance(it)
                .map { balanceStatus ->
                    TotalBalanceUi(
                        balance = CurrencyUiFormatter.formatWithCode(balanceStatus.totalBalance, it),
                        isIncomplete = balanceStatus.isIncomplete
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TotalBalanceUi()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val monthlyBalance: StateFlow<MonthlyBalanceUi> = combine(
        _currentMonth,
        currencyCode
    ) { month, currency ->
        Pair(month, currency)
    }.flatMapLatest {  (month, currency) ->
        transactionUseCase.getMonthlyStats(month, currency)
            .map {
                MonthlyBalanceUi(
                    monthlyIncome = CurrencyUiFormatter.formatWithCode(it.monthlyIncome,currency),
                    monthlySpending = CurrencyUiFormatter.formatWithCode(it.monthlySpending, currency)
                )
            }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MonthlyBalanceUi()
        )

    fun onNextMonth() {
        _currentMonth.update { it.plusMonths(1) }
    }

    fun onPreviousMonth() {
        _currentMonth.update { it.minusMonths(1) }
    }
}