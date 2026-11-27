package com.kevinfreyap.domain.usecase.category

import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.domain.repository.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCategoryUseCase @Inject constructor(
    private val categoryRepository: ICategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> =
        categoryRepository.getAllCategories()
}