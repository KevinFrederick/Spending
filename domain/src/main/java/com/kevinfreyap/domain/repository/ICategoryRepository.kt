package com.kevinfreyap.domain.repository

import com.kevinfreyap.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface ICategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun syncCategoriesFromFirestore()
}