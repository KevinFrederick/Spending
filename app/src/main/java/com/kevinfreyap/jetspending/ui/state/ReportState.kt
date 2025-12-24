package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.jetspending.ui.model.SpendingIncomeBalanceUi

data class ReportState(
    val selectedPeriod: PeriodSelectorOption = PeriodSelectorOption.WEEKLY,
    val rangeLabel: String = "",
    val isNextEnabled: Boolean = true,
    val isPreviousEnabled: Boolean = true,
    val spendingIncomeBalanceUi: SpendingIncomeBalanceUi = SpendingIncomeBalanceUi()
)
