package com.kevinfreyap.data.source.local.model

import androidx.room.Relation
import com.kevinfreyap.data.source.local.entity.DailyRatesEntity
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.TransactionType
import java.math.BigDecimal
import java.time.Instant

data class TransactionMath(
    val amount: BigDecimal,
    val currency: AppCurrency,
    val type: TransactionType,
    val date: Instant,
    val stringDate: String,

    @Relation(
        parentColumn = "stringDate",
        entityColumn = "dateKey"
    )
    val rate: DailyRatesEntity?
)
