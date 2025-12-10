package com.kevinfreyap.data.source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.kevinfreyap.data.source.local.entity.TransactionCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM transaction_category")
    fun getAllCategory(): Flow<List<TransactionCategoryEntity>>

    @Query("SELECT * FROM transaction_category WHERE id = :id")
    suspend fun getCategoryById(id: String): TransactionCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: TransactionCategoryEntity)

    @Upsert
    suspend fun upsertCategories(categories: List<TransactionCategoryEntity>)

    @Query("DELETE FROM transaction_category WHERE id = :id")
    suspend fun deleteCategory(id: String)
}