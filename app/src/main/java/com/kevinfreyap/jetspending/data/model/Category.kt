package com.kevinfreyap.jetspending.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: Int,
    val name: String,
    val icon: ImageVector,
    val type: TransactionType = TransactionType.SPENDING
)