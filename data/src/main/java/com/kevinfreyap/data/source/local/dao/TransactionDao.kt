package com.kevinfreyap.data.source.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import com.kevinfreyap.data.source.local.entity.DailyRatesEntity
import com.kevinfreyap.data.source.local.model.PopulatedTransaction
import com.kevinfreyap.data.source.local.entity.TransactionCategoryEntity
import com.kevinfreyap.data.source.local.entity.TransactionEntity
import com.kevinfreyap.data.source.local.model.TransactionMath
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface TransactionDao {
    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransaction(transactions: List<TransactionEntity>)

    // Read
    @Transaction
    @RawQuery(observedEntities = [
        TransactionEntity::class,
        TransactionCategoryEntity::class,
        DailyRatesEntity::class
    ])
    fun getTransactions(rawQuery: SimpleSQLiteQuery): PagingSource<Int, PopulatedTransaction>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getLatestTransactions(limit: Int): Flow<List<PopulatedTransaction>>

    // Check Rates
    @Query("""
        SELECT DISTINCT t.stringDate
        FROM transactions t
        WHERE t.date NOT IN (SELECT date FROM daily_rates)
    """)
    fun getDatesOfMissingRates(): Flow<List<String>>

    // Summary
    @Query("SELECT amount, currency, type, date, stringDate FROM transactions")
    fun getAllTransactionsForBalance(): Flow<List<TransactionMath>>

    @Query("SELECT amount, currency, type, date, stringDate FROM transactions WHERE date >= :start AND date <= :end")
    fun getTransactionsByTimeFrame(start: Instant, end: Instant): Flow<List<TransactionMath>>

    // Delete
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()

    @Query("DELETE FROM transactions WHERE id IN (:ids)")
    suspend fun deleteTransactionByIds(ids: List<String>)
}