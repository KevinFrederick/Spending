package com.kevinfreyap.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_rates"
)
data class DailyRatesEntity (
    @PrimaryKey
    val dateKey: String,
    val baseCurrency: String,
    val rates: Map<String, Double>,
    val lastUpdated: Long = System.currentTimeMillis()
)