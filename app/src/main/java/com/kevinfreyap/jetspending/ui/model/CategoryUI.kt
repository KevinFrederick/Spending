package com.kevinfreyap.jetspending.ui.model

import androidx.annotation.DrawableRes

data class CategoryUI(
    val id: String,
    val name: String,
    val sortOrder: Int,
    @param:DrawableRes val iconRes: Int
)
