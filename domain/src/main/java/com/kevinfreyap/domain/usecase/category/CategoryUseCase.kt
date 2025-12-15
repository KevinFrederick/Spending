package com.kevinfreyap.domain.usecase.category

import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface CategoryUseCase {
    fun getAllCategories(): Flow<List<Category>>
    fun getCategoryByType(type: TransactionType): Flow<List<Category>>
    suspend fun getCategoryById(categoryId: String): Category?

    suspend fun syncCategoriesFromFirestore()
}