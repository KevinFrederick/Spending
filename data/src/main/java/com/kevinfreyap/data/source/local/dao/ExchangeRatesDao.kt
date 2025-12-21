package com.kevinfreyap.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.kevinfreyap.data.source.local.entity.DailyRatesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRatesDao {
    @Query("SELECT * FROM daily_rates WHERE dateKey = :key")
    fun getExchangeRatesByDate(key: String): Flow<DailyRatesEntity?>

    // Check Availability
    @Query("SELECT * FROM daily_rates WHERE dateKey = :key")
    fun getExchangeRatesOneShot(key: String): DailyRatesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rate: DailyRatesEntity)

    @Upsert
    suspend fun upsertRates(rates: List<DailyRatesEntity>)
}