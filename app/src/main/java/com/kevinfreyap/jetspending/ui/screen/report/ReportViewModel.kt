package com.kevinfreyap.jetspending.ui.screen.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.usecase.currency.CurrencyUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.ui.model.SpendingIncomeBalanceUi
import com.kevinfreyap.jetspending.ui.state.ReportState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.utils.formatter.CurrencyUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    currencyUseCase: CurrencyUseCase,
    private val transactionUseCase: TransactionUseCase,
): ViewModel(){
    private val _selectedCurrency = currencyUseCase.getCurrency()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppCurrency.IDR
        )

    private val _selectedPeriod = MutableStateFlow(PeriodSelectorOption.WEEKLY)
    private val _anchorDate = MutableStateFlow(LocalDate.now())
    private val appMinDate = LocalDate.of(2020, 1, 2)

    @OptIn(ExperimentalCoroutinesApi::class)
    val reportState: StateFlow<UiState<ReportState>> = combine(
        _selectedPeriod,
        _anchorDate,
        _selectedCurrency
    ) { period, date, currency ->
        Triple(period, date, currency)
    }.flatMapLatest { (period, date, currency) ->
        val rangeDateDisplay = DateFormatter.formatRangeDisplay(period, date)

        val minDate = when(period) {
            PeriodSelectorOption.WEEKLY -> LocalDate.now().minusYears(1)
            PeriodSelectorOption.MONTHLY -> LocalDate.now().minusYears(3)
            PeriodSelectorOption.YEARLY -> appMinDate
        }

        val effectiveMinDate = if (minDate.isAfter(appMinDate)) minDate else appMinDate
        val maxDate = LocalDate.now()

        val (start, end) = DateFormatter.formatRangeInstant(
            period = period,
            date = date
        )
        transactionUseCase.getStatsByTimeFrame(start, end, currency)
            .map {
                UiState.Success(
                    ReportState(
                        selectedPeriod = period,
                        rangeLabel = rangeDateDisplay,
                        isPreviousEnabled = canGoPrevious(date, period, effectiveMinDate),
                        isNextEnabled = canGoNext(date, period, maxDate),
                        spendingIncomeBalanceUi = SpendingIncomeBalanceUi(
                            income = CurrencyUiFormatter.formatWithCode(it.income, currency),
                            spending = CurrencyUiFormatter.formatWithCode(it.spending, currency),
                            ratesIncomplete = it.isIncomplete
                        )
                    )
                )
            }
            .onStart {
                UiState.Loading
            }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    fun onSelectPeriod(period: PeriodSelectorOption) {
        _selectedPeriod.value = period
    }

    fun onNextClick() {
        val currentPeriod = _selectedPeriod.value
        val currentSelectedDate = _anchorDate.value

        _anchorDate.value = when(currentPeriod) {
            PeriodSelectorOption.WEEKLY -> currentSelectedDate.plusWeeks(1)
            PeriodSelectorOption.MONTHLY -> currentSelectedDate.plusMonths(1)
            PeriodSelectorOption.YEARLY -> currentSelectedDate.plusYears(1)
        }
    }

    fun onPreviousClick() {
        val currentPeriod = _selectedPeriod.value
        val currentSelectedDate = _anchorDate.value

        _anchorDate.value = when(currentPeriod) {
            PeriodSelectorOption.WEEKLY -> currentSelectedDate.minusWeeks(1)
            PeriodSelectorOption.MONTHLY -> currentSelectedDate.minusMonths(1)
            PeriodSelectorOption.YEARLY -> currentSelectedDate.minusYears(1)
        }
    }

    private fun canGoNext(currentDate: LocalDate, period: PeriodSelectorOption, max: LocalDate): Boolean {
        val endOfCurrent = when(period) {
            PeriodSelectorOption.WEEKLY -> currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            PeriodSelectorOption.MONTHLY -> currentDate.with(TemporalAdjusters.lastDayOfMonth())
            PeriodSelectorOption.YEARLY -> currentDate.with(TemporalAdjusters.lastDayOfYear())
        }
        return endOfCurrent.isBefore(max)
    }

    private fun canGoPrevious(currentDate: LocalDate, period: PeriodSelectorOption, min: LocalDate): Boolean {
        val startOfCurrent = when(period) {
            PeriodSelectorOption.WEEKLY -> currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            PeriodSelectorOption.MONTHLY -> currentDate.with(TemporalAdjusters.firstDayOfMonth())
            PeriodSelectorOption.YEARLY -> currentDate.with(TemporalAdjusters.firstDayOfYear())
        }
        return startOfCurrent.isAfter(min)
    }
}