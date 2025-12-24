package com.kevinfreyap.jetspending.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.usecase.currency.CurrencyUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.ui.model.DashboardUi
import com.kevinfreyap.jetspending.ui.model.SpendingIncomeBalanceUi
import com.kevinfreyap.jetspending.ui.model.TotalBalanceUi
import com.kevinfreyap.jetspending.ui.state.UiState
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
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    transactionUseCase: TransactionUseCase,
    currencyUseCase: CurrencyUseCase,
    private val transactionItemUiMapper: TransactionItemUiMapper
): ViewModel() {
    private val currencyCodeFlow = currencyUseCase.getCurrency()

    private val latestTransactionsFlow = combine(
        transactionUseCase.getLatestTransactions(),
        currencyCodeFlow
    ) { transactions, currency ->
        transactions.map { transaction ->
            transactionItemUiMapper.mapTransactionDomainToUi(transaction, currency)
        }
    }

    private val _currentMonth = MutableStateFlow(LocalDate.now())

    val nextBtnEnabled: StateFlow<Boolean> = _currentMonth.map { selectedMonth ->
        val now = LocalDate.now()
            .withDayOfMonth(1)
        selectedMonth.withDayOfMonth(1).isBefore(now)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val previousBtnEnabled: StateFlow<Boolean> = _currentMonth.map { selectedMonth ->
        val minYear = LocalDate.now()
            .with(TemporalAdjusters.firstDayOfYear())

        selectedMonth.withDayOfMonth(1).isAfter(minYear)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val monthDisplay = _currentMonth.map {
        DateFormatter.formatMonthToString(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DateFormatter.formatMonthToString(LocalDate.now())
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val totalBalanceFlow = currencyCodeFlow
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
    private val monthlyBalanceFlow = combine(
        _currentMonth,
        currencyCodeFlow
    ) { month, currency ->
        Pair(month, currency)
    }.flatMapLatest {  (month, currency) ->
        val (startDate, endDate) = DateFormatter.formatRangeInstant(PeriodSelectorOption.MONTHLY, month)
        transactionUseCase.getStatsByTimeFrame(startDate, endDate, currency)
            .map {
                SpendingIncomeBalanceUi(
                    income = CurrencyUiFormatter.formatWithCode(it.income,currency),
                    spending = CurrencyUiFormatter.formatWithCode(it.spending, currency)
                )
            }
    }

    val uiState: StateFlow<UiState<DashboardUi>> = combine(
        totalBalanceFlow,
        monthlyBalanceFlow,
        latestTransactionsFlow
    ) { total, monthly, transactions ->
        val data = DashboardUi(
            totalBalance = total,
            monthlyBalance = monthly,
            latestTransactions = transactions
        )

        UiState.Success(data)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    fun onNextMonth() {
        _currentMonth.update { it.plusMonths(1) }
    }

    fun onPreviousMonth() {
        _currentMonth.update { it.minusMonths(1) }
    }
}