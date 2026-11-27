package com.kevinfreyap.jetspending.ui.action

import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.model.TransactionType
import java.time.LocalDate

sealed interface ReportsAction {
    data class OnPeriodSelected(val period: PeriodSelectorOption): ReportsAction
    data class OnTypeSelected(val type: TransactionType): ReportsAction
    data class OnShowDateDialog(val isVisible: Boolean): ReportsAction
    data class OnSelectDate(val date: LocalDate): ReportsAction
    data class OnBarClicked(val barId: String): ReportsAction
    data object OnClearSelectedBar: ReportsAction
    data object OnPreviousPeriod: ReportsAction
    data object OnNextPeriod: ReportsAction
    data object OnDismissWeeklyBanner: ReportsAction
}