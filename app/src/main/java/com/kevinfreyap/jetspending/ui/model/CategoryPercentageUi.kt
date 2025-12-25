package com.kevinfreyap.jetspending.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color

data class CategoryPercentageUi(
    val categoryAmount: String = "",
    val categoryColor: Color = Color.Unspecified,
    val percentage: Float = 0f,
    @param:StringRes val name: Int,
    @param:DrawableRes val iconRes: Int
)
