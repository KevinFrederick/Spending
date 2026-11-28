package com.kevinfreyap.data.source.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.kevinfreyap.data.source.local.entity.DailyRatesEntity
import com.kevinfreyap.data.source.local.entity.TransactionEntity

data class PopulatedTransaction(
    // Embeds the main Transaction data (amount, date, note, etc.)
    @Embedded
    val transaction: TransactionEntity,

    @Relation(
        parentColumn = "stringDate",
        entityColumn = "dateKey",
    )
    val rate: DailyRatesEntity?
)