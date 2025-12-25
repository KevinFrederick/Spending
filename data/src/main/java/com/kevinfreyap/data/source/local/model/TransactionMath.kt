package com.kevinfreyap.data.source.local.model

import androidx.room.Relation
import com.kevinfreyap.data.source.local.entity.DailyRatesEntity
import com.kevinfreyap.data.source.local.entity.TransactionCategoryEntity
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import java.math.BigDecimal
import java.time.Instant

data class TransactionMath(
    val amount: BigDecimal,
    val currency: AppCurrency,
    val type: TransactionType,
    val categoryId: String,
    val date: Instant,
    val stringDate: String,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: TransactionCategoryEntity,

    @Relation(
        parentColumn = "stringDate",
        entityColumn = "dateKey"
    )
    val rate: DailyRatesEntity?
)
