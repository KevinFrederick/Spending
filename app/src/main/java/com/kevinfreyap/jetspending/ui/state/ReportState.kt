package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.model.CategoryPercentageUi
import com.kevinfreyap.jetspending.ui.model.ChartDataUi
import com.kevinfreyap.jetspending.ui.model.SpendingIncomeBalanceUi

data class ReportState(
    val selectedPeriod: PeriodSelectorOption = PeriodSelectorOption.WEEKLY,
    val rangeLabel: String = "",
    val isNextEnabled: Boolean = true,
    val isPreviousEnabled: Boolean = true,
    val spendingIncomeBalanceUi: SpendingIncomeBalanceUi = SpendingIncomeBalanceUi(),
    val chartData: List<ChartDataUi> = emptyList(),
    val selectedCategoryType: TransactionType = TransactionType.SPENDING,
    val categoryList: List<CategoryPercentageUi> = emptyList()
)