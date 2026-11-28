package com.kevinfreyap.jetspending.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kevinfreyap.domain.model.TransactionType

data class CategoryUI(
    val id: String,
    val sortOrder: Int,
    @param:StringRes val name: Int,
    @param:DrawableRes val iconRes: Int,
    val type: TransactionType
)
