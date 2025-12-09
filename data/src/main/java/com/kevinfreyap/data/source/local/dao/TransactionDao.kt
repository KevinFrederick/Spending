package com.kevinfreyap.data.source.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SimpleSQLiteQuery
import com.kevinfreyap.data.source.local.entity.TransactionCategoryEntity
import com.kevinfreyap.data.source.local.entity.TransactionEntity
import com.kevinfreyap.data.source.local.entity.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    // Read
    @Transaction
    @RawQuery(observedEntities = [TransactionEntity::class, TransactionCategoryEntity::class])
    fun getTransactions(rawQuery: SimpleSQLiteQuery): PagingSource<Int, TransactionWithCategory>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getLatestTransactions(limit: Int): Flow<List<TransactionWithCategory>>
}