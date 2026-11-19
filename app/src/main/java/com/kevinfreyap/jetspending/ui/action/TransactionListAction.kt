package com.kevinfreyap.jetspending.ui.action

import com.kevinfreyap.jetspending.ui.screen.list.state.DatePickerTarget
import com.kevinfreyap.jetspending.ui.screen.list.state.FilterState

sealed interface TransactionListAction {
    data class OnQueryChange(val query: String): TransactionListAction
    data class OnToggleFilterSheet(val isVisible: Boolean): TransactionListAction
    data class OnApplyFilter(val filter: FilterState): TransactionListAction
}