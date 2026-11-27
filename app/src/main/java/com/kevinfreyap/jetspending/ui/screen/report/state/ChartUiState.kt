package com.kevinfreyap.jetspending.ui.screen.report.state

import androidx.annotation.StringRes
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.R

data class ChartUiState(
    @StringRes val titleRes: Int = R.string.label_chart_title_monthly_expense,
    val maxAmount: String = "",
    val bars: List<ChartBarItemState> = emptyList(),
    val selectedCurrency: AppCurrency = AppCurrency.DEFAULT,
    val hasAnyData: Boolean = false
)