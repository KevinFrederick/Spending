package com.kevinfreyap.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kevinfreyap.domain.model.TransactionType

@Entity(tableName = "transaction_category")
data class TransactionCategoryEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val iconId: String,
    val sortOrder: Int,
    val types: List<TransactionType>
)
