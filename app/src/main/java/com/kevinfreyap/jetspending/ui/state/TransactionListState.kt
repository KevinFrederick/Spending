package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.jetspending.ui.helper.category.toCategoryUI
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import com.kevinfreyap.jetspending.ui.screen.list.state.FilterState

data class TransactionListState(
    val isFilterSheetVisible: Boolean = false,
    val filterState: FilterState = FilterState(),
    val categories: List<CategoryUI> = Category.allSorted().map { it.toCategoryUI() },
    val query: String = ""
)
