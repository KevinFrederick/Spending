package com.kevinfreyap.jetspending.ui.state

data class DashboardState(
    val greetings: String,
    val date: String,
    val totalBalance: String,
    val currency: String,
    val monthlySummary: MonthlySummaryState,
    val recentTransactions: List<TransactionItemState>
)
