package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.jetspending.ui.helper.date.DayGreeting

data class DashboardState(
    val userName: String,
    val greetings: DayGreeting,
    val date: String,
    val totalBalance: String,
    val isRatesMissing: Boolean,
    val currency: String,
    val monthlySummary: MonthlySummaryState,
    val recentTransactions: List<TransactionItemState>
)
