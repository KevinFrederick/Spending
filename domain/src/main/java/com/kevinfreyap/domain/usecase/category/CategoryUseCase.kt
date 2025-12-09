package com.kevinfreyap.domain.usecase.category

import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface CategoryUseCase {
    fun getCategoryByType(type: TransactionType): Flow<List<Category>>
    suspend fun syncCategoriesFromFirestore()
}