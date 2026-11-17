package com.kevinfreyap.jetspending.ui.screen.dashboard.state

import com.kevinfreyap.jetspending.ui.state.SummaryState

data class MonthlySummaryState(
    val incomeSummary: SummaryState,
    val spendingSummary: SummaryState
)
