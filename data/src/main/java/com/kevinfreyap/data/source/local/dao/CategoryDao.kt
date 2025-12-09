package com.kevinfreyap.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kevinfreyap.data.source.local.entity.TransactionCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM transaction_category")
    fun getAllCategory(): Flow<List<TransactionCategoryEntity>>

    @Query("SELECT * FROM transaction_category WHERE id = :id")
    fun getCategoryById(id: String): Flow<TransactionCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<TransactionCategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: TransactionCategoryEntity)

    @Query("DELETE FROM transaction_category WHERE id = :id")
    suspend fun deleteCategory(id: String)
}