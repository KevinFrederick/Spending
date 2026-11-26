package com.kevinfreyap.jetspending.ui.screen.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.PeriodReportData
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.usecase.currency.GetCurrencyUseCase
import com.kevinfreyap.domain.usecase.reports.GetPeriodReportUseCase
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.ui.action.ReportsAction
import com.kevinfreyap.jetspending.ui.helper.category.toCategoryUI
import com.kevinfreyap.jetspending.ui.helper.currency.toCurrencyString
import com.kevinfreyap.jetspending.ui.helper.period.formatHeaderLabel
import com.kevinfreyap.jetspending.ui.helper.period.isAfterCurrentPeriod
import com.kevinfreyap.jetspending.ui.helper.period.step
import com.kevinfreyap.jetspending.ui.helper.period.toInstantRange
import com.kevinfreyap.jetspending.ui.helper.state.toReportsContentState
import com.kevinfreyap.jetspending.ui.screen.report.state.ReportQueryFilter
import com.kevinfreyap.jetspending.ui.screen.report.state.ReportsContentState
import com.kevinfreyap.jetspending.ui.screen.report.state.WeeklySummaryBannerState
import com.kevinfreyap.jetspending.ui.state.ReportsState
import com.kevinfreyap.jetspending.ui.state.UiState
import com.kevinfreyap.jetspending.ui.state.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCurrency: GetCurrencyUseCase,
    private val getPeriodReport: GetPeriodReportUseCase,
): ViewModel(){
    private val fromWeeklyNotification: Boolean = savedStateHandle.get<Boolean>("fromWeeklyNotification") ?: false

    private val initialAnchorDate: LocalDate = if (fromWeeklyNotification) {
        val today = LocalDate.now()
        if (today.dayOfWeek == DayOfWeek.SUNDAY) today else today.minusWeeks(1)
    } else {
        LocalDate.now()
    }

    private val _filterQuery = MutableStateFlow(
        ReportQueryFilter(
            selectedPeriod = if (fromWeeklyNotification) PeriodSelectorOption.WEEKLY else PeriodSelectorOption.MONTHLY,
            anchorDate = initialAnchorDate
        )
    )

    private val _isWeeklyBannerDismissed = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val weeklyBannerFlow: Flow<WeeklySummaryBannerState?> = if (fromWeeklyNotification) {
        val (curStart, curEnd) = PeriodSelectorOption.WEEKLY.toInstantRange(initialAnchorDate)
        val (prevStart, prevEnd) = PeriodSelectorOption.WEEKLY.toInstantRange(initialAnchorDate.minusWeeks(1))

        getCurrency().distinctUntilChanged().flatMapLatest { currency ->
            combine(
                getPeriodReport(curStart, curEnd, PeriodSelectorOption.WEEKLY, currency),
                getPeriodReport(prevStart, prevEnd, PeriodSelectorOption.WEEKLY, currency),
                _isWeeklyBannerDismissed
            ) { curReport, prevReport, isDismissed ->
                if (isDismissed) {
                    null
                } else {
                    buildWeeklySummaryBannerState(
                        currency = currency,
                        curReport = curReport,
                        prevReport = prevReport,
                        anchorDate = initialAnchorDate
                    )
                }
            }
        }
    } else {
        flowOf(null)
    }

    // Only triggers a new DB query when period, anchorDate, or currency changes.
    private val queryTriggerFlow = combine(
        _filterQuery.distinctUntilChangedBy { it.selectedPeriod to it.anchorDate },
        getCurrency().distinctUntilChanged()
    ) { filter, currency ->
        Triple(filter.selectedPeriod, filter.anchorDate, currency)
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val contentFlow: Flow<UiState<ReportsContentState>> = queryTriggerFlow
        .flatMapLatest { (period, anchorDate, currency) ->
            val (startInstant, endInstant) = period.toInstantRange(anchorDate)

            getPeriodReport(
                startDate = startInstant,
                endDate = endInstant,
                period = period,
                selectedCurrency = currency
            ).combine(_filterQuery) { reportData, latestFilter ->
                UiState.Success(
                    reportData.toReportsContentState(
                        filter = latestFilter,
                        selectedCurrency = currency
                    )
                ) as UiState<ReportsContentState>
            }
        }
        .catch { throwable ->
            emit(UiState.Failure(throwable))
        }

    val state: StateFlow<ReportsState> = combine(
        _filterQuery,
        contentFlow,
        weeklyBannerFlow
    ) { filter, content, weeklyBanner ->
        val today = LocalDate.now()
        val nextDate = filter.selectedPeriod.step(filter.anchorDate, 1)
        val canGoNext = !filter.selectedPeriod.isAfterCurrentPeriod(nextDate, today)

        ReportsState(
            queryFilter = filter,
            periodLabel = filter.selectedPeriod.formatHeaderLabel(filter.anchorDate),
            canGoNext = canGoNext,
            weeklyBanner = weeklyBanner,
            content = content
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportsState()
    )

    fun onAction(action: ReportsAction) {
        when(action) {
            is ReportsAction.OnPeriodSelected -> {
                _filterQuery.update { currentState ->
                    currentState.copy(
                        selectedPeriod = action.period,
                        anchorDate = LocalDate.now(),
                        selectedBarId = null,
                    )
                }
            }
            is ReportsAction.OnTypeSelected -> {
                _filterQuery.update { currentState ->
                    currentState.copy(
                        selectedType = action.type,
                        selectedBarId = null
                    )
                }
            }
            is ReportsAction.OnBarClicked -> {
                _filterQuery.update { currentState ->
                    val newBarId = if (currentState.selectedBarId == action.barId) null else action.barId

                    currentState.copy(
                        selectedBarId = newBarId
                    )
                }
            }
            is ReportsAction.OnShowDateDialog -> {
                _filterQuery.update { currentState ->
                    currentState.copy(
                        isDateDialogVisible = action.isVisible,
                        selectedBarId = null
                    )
                }
            }
            is ReportsAction.OnSelectDate -> {
                _filterQuery.update { currentState ->
                    currentState.copy(
                        anchorDate = action.date,
                        selectedBarId = null,
                        isDateDialogVisible = false
                    )
                }
            }
            ReportsAction.OnClearSelectedBar -> {
                _filterQuery.update { currentState ->
                    currentState.copy(
                        selectedBarId = null
                    )
                }
            }
            ReportsAction.OnNextPeriod -> {
                val today = LocalDate.now()
                _filterQuery.update { currentState ->
                    val nextDate = currentState.selectedPeriod.step(currentState.anchorDate, 1)
                    if (!currentState.selectedPeriod.isAfterCurrentPeriod(nextDate, today)) {
                        currentState.copy(
                            anchorDate = nextDate,
                            selectedBarId = null
                        )
                    } else {
                        currentState
                    }
                }
            }
            ReportsAction.OnPreviousPeriod -> {
                _filterQuery.update { currentState ->
                    val previousDate = currentState.selectedPeriod.step(currentState.anchorDate, -1)
                    currentState.copy(
                        anchorDate = previousDate,
                        selectedBarId = null
                    )
                }
            }
            ReportsAction.OnDismissWeeklyBanner -> {
                _isWeeklyBannerDismissed.value = true
            }
        }
    }

    private fun buildWeeklySummaryBannerState(
        currency: AppCurrency,
        curReport: PeriodReportData,
        prevReport: PeriodReportData,
        anchorDate: LocalDate
    ): WeeklySummaryBannerState {
        val dateRangeLabel = PeriodSelectorOption.WEEKLY.formatHeaderLabel(anchorDate)

        val curExpense = curReport.totalExpense
        val prevExpense = prevReport.totalExpense

        val isLowerSpending: Boolean
        val headlineText: UiText

        if (prevExpense > BigDecimal.ZERO) {
            if (curExpense < prevExpense) {
                isLowerSpending = true
                val diff = prevExpense.subtract(curExpense)
                val pct = diff.multiply(BigDecimal("100"))
                    .divide(prevExpense, 0, RoundingMode.HALF_UP)
                    .toInt()
                headlineText = UiText.StringResource(R.string.weekly_banner_spent_less, pct)
            } else if (curExpense > prevExpense) {
                isLowerSpending = false
                val diff = curExpense.subtract(prevExpense)
                val diffFormatted = diff.toCurrencyString(currency)
                headlineText = UiText.StringResource(R.string.weekly_banner_spent_more, diffFormatted)
            } else {
                isLowerSpending = true
                headlineText = UiText.StringResource(R.string.weekly_banner_spent_same)
            }
        } else {
            isLowerSpending = true
            val spendingCategories = curReport.categories.filter { it.type == TransactionType.SPENDING }
            val count = spendingCategories.size
            val curExpenseFormatted = curExpense.toCurrencyString(currency)
            headlineText = if (count == 1) {
                UiText.StringResource(R.string.weekly_banner_fallback_single, curExpenseFormatted)
            } else {
                UiText.StringResource(R.string.weekly_banner_fallback_plural, curExpenseFormatted, count)
            }
        }

        val topCategory = curReport.categories.firstOrNull { it.type == TransactionType.SPENDING }
        val supportingText = if (topCategory != null) {
            val catNameRes = topCategory.category.toCategoryUI().name
            val catName = UiText.StringResource(catNameRes)
            val catAmount = topCategory.amount.toCurrencyString(currency)
            UiText.StringResource(R.string.weekly_banner_top_category, catName, catAmount)
        } else {
            UiText.StringResource(R.string.weekly_banner_no_expense)
        }

        return WeeklySummaryBannerState(
            dateRangeLabel = dateRangeLabel,
            headlineText = headlineText,
            supportingText = supportingText,
            isLowerSpending = isLowerSpending
        )
    }
}