package com.kevinfreyap.jetspending.ui.screen.report.state

import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.model.TransactionType
import java.time.LocalDate

data class ReportQueryFilter(
    val selectedPeriod: PeriodSelectorOption = PeriodSelectorOption.MONTHLY,
    val selectedType: TransactionType = TransactionType.SPENDING,
    val anchorDate: LocalDate = LocalDate.now(),
    val selectedBarId: String? = null,
    val isDateDialogVisible: Boolean = false
)