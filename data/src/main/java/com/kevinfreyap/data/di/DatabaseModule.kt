package com.kevinfreyap.data.di

import android.content.Context
import androidx.room.Room
import com.kevinfreyap.data.source.local.db.AppDatabase
import com.kevinfreyap.data.source.local.dao.ExchangeRatesDao
import com.kevinfreyap.data.source.local.dao.TransactionDao
import com.kevinfreyap.data.source.local.db.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "spending.db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideExchangeRateDao(database: AppDatabase): ExchangeRatesDao = database.exchangeRateDao()
}