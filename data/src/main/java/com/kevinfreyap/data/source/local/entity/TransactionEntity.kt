package com.kevinfreyap.data.source.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import java.math.BigDecimal
import java.time.Instant

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["date"])
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val amount: BigDecimal,
    val currency: AppCurrency,
    val type: TransactionType,
    val categoryId: String,
    val date: Instant,
    val stringDate: String,
    val notes: String = "",
    val lastUpdated: Long
)
