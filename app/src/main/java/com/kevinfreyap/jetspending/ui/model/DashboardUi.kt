package com.kevinfreyap.jetspending.ui.model

data class DashboardUi(
    val totalBalance: TotalBalanceUi = TotalBalanceUi(),
    val monthlyBalance: MonthlyBalanceUi = MonthlyBalanceUi(),
    val latestTransactions: List<TransactionItemUi> = emptyList()
)
