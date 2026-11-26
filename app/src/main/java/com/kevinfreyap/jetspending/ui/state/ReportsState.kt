package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.jetspending.ui.screen.report.state.ReportQueryFilter
import com.kevinfreyap.jetspending.ui.screen.report.state.ReportsContentState
import com.kevinfreyap.jetspending.ui.screen.report.state.WeeklySummaryBannerState

data class ReportsState(
    val queryFilter: ReportQueryFilter = ReportQueryFilter(),
    val periodLabel: String = "",
    val canGoNext: Boolean = true,
    val weeklyBanner: WeeklySummaryBannerState? = null,
    val content: UiState<ReportsContentState> = UiState.Loading
)
