package com.kevinfreyap.jetspending.ui.screen.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.usecase.currency.GetCurrencyUseCase
import com.kevinfreyap.domain.usecase.transaction.GetRecentTransactionsUseCase
import com.kevinfreyap.domain.usecase.transaction.GetStatusByTimeUseCase
import com.kevinfreyap.domain.usecase.transaction.GetTotalBalanceUseCase
import com.kevinfreyap.domain.usecase.user.GetUserUseCase
import com.kevinfreyap.jetspending.ui.helper.currency.toCurrencyString
import com.kevinfreyap.jetspending.ui.helper.date.DayGreeting
import com.kevinfreyap.jetspending.ui.helper.date.toFullMonthYear
import com.kevinfreyap.jetspending.ui.helper.state.toMonthlySummary
import com.kevinfreyap.jetspending.ui.helper.state.toTransactionItemState
import com.kevinfreyap.jetspending.ui.state.DashboardState
import com.kevinfreyap.jetspending.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getUser: GetUserUseCase,
    getTotalBalance: GetTotalBalanceUseCase,
    getCurrency: GetCurrencyUseCase,
    getStatusByTime: GetStatusByTimeUseCase,
    getRecentTransactions: GetRecentTransactionsUseCase
): ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<UiState<DashboardState>> = getCurrency()
        .distinctUntilChanged()
        .flatMapLatest { selectedCurrency ->
            val now = Instant.now()
            val monthRange = PeriodSelectorOption.MONTHLY.toInstantRange(reference = now)

            combine(
                getUser(),
                getTotalBalance(selectedCurrency = selectedCurrency),
                getStatusByTime(
                    startDate = monthRange.start,
                    endDate = monthRange.end,
                    selectedCurrency = selectedCurrency
                ),
                getRecentTransactions(selectedCurrency = selectedCurrency)
            ) { user, balanceStatus, monthlySummary, transactions ->
                val currentInstant = Instant.now()
                val hour = currentInstant
                    .atZone(ZoneId.systemDefault())
                    .hour

                val resultState: UiState<DashboardState> = UiState.Success(
                    DashboardState(
                        userName = user?.displayName ?: "User",
                        greetings = DayGreeting.fromHour(hour),
                        date = currentInstant.toFullMonthYear(),
                        totalBalance = balanceStatus.totalBalance.toCurrencyString(selectedCurrency),
                        isRatesMissing = balanceStatus.isIncomplete,
                        currency = selectedCurrency.name,
                        monthlySummary = monthlySummary.toMonthlySummary(
                            selectedCurrency = selectedCurrency,
                            instant = now
                        ),
                        recentTransactions = transactions.map { transaction ->
                            transaction.toTransactionItemState(selectedCurrency)
                        }
                    )
                )

                Log.d("Dashboard", user?.displayName ?: "Nothing")

                resultState
            }
        }
        .catch { throwable ->
            emit(UiState.Failure(throwable))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )
}