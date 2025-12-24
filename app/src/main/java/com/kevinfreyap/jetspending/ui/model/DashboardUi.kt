package com.kevinfreyap.jetspending.ui.model

data class DashboardUi(
    val totalBalance: TotalBalanceUi = TotalBalanceUi(),
    val monthlyBalance: SpendingIncomeBalanceUi = SpendingIncomeBalanceUi(),
    val latestTransactions: List<TransactionItemUi> = emptyList()
)
