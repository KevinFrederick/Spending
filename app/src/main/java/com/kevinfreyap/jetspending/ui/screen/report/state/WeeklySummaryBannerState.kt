package com.kevinfreyap.jetspending.ui.screen.report.state

import com.kevinfreyap.jetspending.ui.state.UiText

data class WeeklySummaryBannerState(
    val dateRangeLabel: String,
    val headlineText: UiText,
    val supportingText: UiText,
    val isLowerSpending: Boolean
)
