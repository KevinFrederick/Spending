package com.kevinfreyap.jetspending.ui.model

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.model.TransactionType
import java.time.LocalDate

data class ReportParams(
    val period: PeriodSelectorOption, // Replace with your actual types
    val date: LocalDate,
    val categoryType: TransactionType,
    val currency: AppCurrency
)
