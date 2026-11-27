package com.kevinfreyap.jetspending.ui.screen.report.state

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.ui.model.CategoryUI

data class CategoryPercentageItemState(
    val categoryUI: CategoryUI,
    val percentage: Float,
    val percentageText: String,
    val amount: String,
    val selectedCurrency: AppCurrency
)
