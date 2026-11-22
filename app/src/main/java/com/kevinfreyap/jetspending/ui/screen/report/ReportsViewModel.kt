package com.kevinfreyap.jetspending.ui.screen.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.usecase.reports.GetPeriodReportUseCase
import com.kevinfreyap.domain.usecase.currency.GetCurrencyUseCase
import com.kevinfreyap.jetspending.ui.action.ReportsAction
import com.kevinfreyap.jetspending.ui.helper.period.formatHeaderLabel
import com.kevinfreyap.jetspending.ui.helper.period.isAfterCurrentPeriod
import com.kevinfreyap.jetspending.ui.helper.period.step
import com.kevinfreyap.jetspending.ui.helper.period.toInstantRange
import com.kevinfreyap.jetspending.ui.helper.state.toReportsContentState
import com.kevinfreyap.jetspending.ui.screen.report.state.ReportQueryFilter
import com.kevinfreyap.jetspending.ui.screen.report.state.ReportsContentState
import com.kevinfreyap.jetspending.ui.state.ReportsState
import com.kevinfreyap.jetspending.ui.state.UiState
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    getCurrency: GetCurrencyUseCase,
    private val getPeriodReport: GetPeriodReportUseCase,
): ViewModel(){
    private val _filterQuery = MutableStateFlow(ReportQueryFilter())

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
        contentFlow
    ) {filter, content ->
        val today = LocalDate.now()
        val nextDate = filter.selectedPeriod.step(filter.anchorDate, 1)
        val canGoNext = !filter.selectedPeriod.isAfterCurrentPeriod(nextDate, today)

        ReportsState(
            queryFilter = filter,
            periodLabel = filter.selectedPeriod.formatHeaderLabel(filter.anchorDate),
            canGoNext = canGoNext,
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
        }
    }
}