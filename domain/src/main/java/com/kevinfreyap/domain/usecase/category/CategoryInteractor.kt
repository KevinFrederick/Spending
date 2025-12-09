package com.kevinfreyap.domain.usecase.category

import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.repository.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryInteractor @Inject constructor(
    private val categoryRepository: ICategoryRepository
): CategoryUseCase{
    override fun getCategoryByType(type: TransactionType): Flow<List<Category>> {
        return categoryRepository.getAllCategories().map { categories ->
            categories.filter { category ->
                category.types.contains(type)
            }
        }
    }

    override suspend fun syncCategoriesFromFirestore() {
        categoryRepository.syncCategoriesFromFirestore()
    }
}