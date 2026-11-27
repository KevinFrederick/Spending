package com.kevinfreyap.domain.usecase.sync

import com.kevinfreyap.domain.repository.ICategoryRepository
import javax.inject.Inject

class SyncCategoriesUseCase @Inject constructor(
    private val categoryRepository: ICategoryRepository
) {
    suspend operator fun invoke() = categoryRepository.syncCategoriesFromFirestore()
}