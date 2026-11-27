package com.kevinfreyap.jetspending.ui.screen.report.state

data class ReportsContentState(
    val summaryState: ReportTotalSummaryState = ReportTotalSummaryState(),
    val chartState: ChartUiState = ChartUiState(),
    val categoriesState: List<CategoryPercentageItemState> = emptyList(),
    val hasMissingRates: Boolean = false,
)
