package com.kevinfreyap.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kevinfreyap.data.source.local.converter.BigDecimalConverter
import com.kevinfreyap.data.source.local.converter.CurrencyConverter
import com.kevinfreyap.data.source.local.converter.DateConverter
import com.kevinfreyap.data.source.local.converter.ExchangeRatesConverter
import com.kevinfreyap.data.source.local.converter.TransactionTypeConverter
import com.kevinfreyap.data.source.local.converter.TransactionTypeListConverter
import com.kevinfreyap.data.source.local.dao.CategoryDao
import com.kevinfreyap.data.source.local.dao.ExchangeRatesDao
import com.kevinfreyap.data.source.local.dao.TransactionDao
import com.kevinfreyap.data.source.local.entity.DailyRatesEntity
import com.kevinfreyap.data.source.local.entity.TransactionCategoryEntity
import com.kevinfreyap.data.source.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        TransactionCategoryEntity::class,
        DailyRatesEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    // Handle Room <-> DB
    BigDecimalConverter::class,
    CurrencyConverter::class,
    TransactionTypeConverter::class,
    TransactionTypeListConverter::class,
    DateConverter::class,
    ExchangeRatesConverter::class
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun exchangeRateDao(): ExchangeRatesDao
}