package com.kevinfreyap.domain.usecase.category

import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.repository.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategoryByTypeUseCase @Inject constructor(
    private val categoryRepository: ICategoryRepository
) {
    operator fun invoke(type: TransactionType): Flow<List<Category>> {
        return categoryRepository.getAllCategories().map { categories ->
            categories.filter { category ->
                category.types.contains(type)
            }
        }
    }
}