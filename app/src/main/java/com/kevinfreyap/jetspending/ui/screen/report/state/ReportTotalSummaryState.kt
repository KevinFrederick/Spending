package com.kevinfreyap.jetspending.ui.screen.report.state

import com.kevinfreyap.domain.model.AppCurrency

data class ReportTotalSummaryState(
    val totalNet: String = "",
    val totalIncome: String = "",
    val totalSpending: String = "",
    val selectedCurrency: AppCurrency = AppCurrency.DEFAULT,
    val isNetPositive: Boolean = false,
    val isNetNegative: Boolean = false,
    val hasSpending: Boolean = false,
    val hasIncome: Boolean = false
)
