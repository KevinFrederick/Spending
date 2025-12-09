package com.kevinfreyap.domain.model

data class Category(
    val id: String,
    val name: String,
    val iconId: String,
    val sortOrder: Int,
    val types: List<TransactionType>
)