package com.kevinfreyap.jetspending.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class CategoryUI(
    val id: String,
    val sortOrder: Int,
    @param:StringRes val name: Int,
    @param:DrawableRes val iconRes: Int
)
