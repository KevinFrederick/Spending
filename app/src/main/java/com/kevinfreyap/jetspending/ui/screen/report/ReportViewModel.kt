package com.kevinfreyap.jetspending.ui.screen.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.ChartData
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.usecase.currency.CurrencyUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.jetspending.ui.model.CategoryPercentageUi
import com.kevinfreyap.jetspending.ui.model.ReportParams
import com.kevinfreyap.jetspending.ui.model.SpendingIncomeBalanceUi
import com.kevinfreyap.jetspending.ui.state.ReportState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.utils.formatter.CategoryUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.ChartUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.CurrencyUiFormatter
import com.kevinfreyap.jetspending.utils.formatter.DateFormatter
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
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
    private val _selectedCategoryType = MutableStateFlow(TransactionType.SPENDING)
    private val appMinDate = LocalDate.of(2020, 1, 2)

    val chartProducer = CartesianChartModelProducer()

    @OptIn(ExperimentalCoroutinesApi::class)
    val reportState: StateFlow<UiState<ReportState>> = combine(
        _selectedPeriod,
        _anchorDate,
        _selectedCategoryType,
        _selectedCurrency
    ) { period, date, categoryType, currency ->
        ReportParams(
            period = period,
            date = date,
            categoryType = categoryType,
            currency = currency
        )
    }.flatMapLatest { (period, date, categoryType, currency) ->
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

        val statsFlow = transactionUseCase.getStatsByTimeFrame(
            startDate = start,
            endDate = end,
            selectedCurrency = currency
        )

        val chartDataFlow = transactionUseCase.getChartData(
            period = period,
            startDate = start,
            endDate = end,
            selectedCurrency = currency
        )

        val categoryFlow = transactionUseCase.getCategories(
            period = period,
            startDate = start,
            endDate = end,
            selectedType = categoryType,
            selectedCurrency = currency
        )

        combine(
            statsFlow,
            chartDataFlow,
            categoryFlow
        ) { stats, chart, category ->
            updateChart(chart)

            val uiLabels = chart.map {
                ChartUiFormatter.mapChartDomainToUi(it, period)
            }

            Triple(stats, uiLabels, category)
        }
            .map { (stats, chartUi, category) ->
                UiState.Success(
                    ReportState(
                        selectedPeriod = period,
                        rangeLabel = rangeDateDisplay,
                        isPreviousEnabled = canGoPrevious(date, period, effectiveMinDate),
                        isNextEnabled = canGoNext(date, period, maxDate),
                        spendingIncomeBalanceUi = SpendingIncomeBalanceUi(
                            income = CurrencyUiFormatter.formatWithCode(stats.income, currency),
                            spending = CurrencyUiFormatter.formatWithCode(stats.spending, currency),
                            ratesIncomplete = stats.isIncomplete
                        ),
                        chartData = chartUi,
                        selectedCategoryType = categoryType,
                        categoryList = category.map { categoryPercentage -> 
                            CategoryPercentageUi(
                                categoryAmount = CurrencyUiFormatter.formatWithCode(categoryPercentage.amount, currency),
                                categoryColor = CategoryUiFormatter.getBackgroundColor(categoryPercentage.type),
                                percentage = categoryPercentage.percentage,
                                name = CategoryUiFormatter.mapCategoryNameToString(categoryPercentage.categoryId),
                                iconRes = CategoryUiFormatter.mapIconIdToDrawable(categoryPercentage.categoryIconId)
                            )
                        }
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

    fun onTypeSelected(type: TransactionType) {
        _selectedCategoryType.value = type
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

    private suspend fun updateChart(
        chartData: List<ChartData>
    ) {
        chartProducer.runTransaction {
            val xValues = chartData.map { it.index }
            val incomeValues = chartData.map { it.amount.income }
            val spendingValues = chartData.map { it.amount.spending }

            columnSeries {
                series(xValues, incomeValues)
                series(xValues, spendingValues)
            }
        }
    }
}