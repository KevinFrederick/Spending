package com.kevinfreyap.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kevinfreyap.data.mapper.TransactionCategoryMapper
import com.kevinfreyap.data.source.local.dao.CategoryDao
import com.kevinfreyap.data.source.remote.firebase.TransactionCategoryFirestore
import com.kevinfreyap.data.utils.DataConstants.CATEGORY_COLLECTION
import com.kevinfreyap.domain.error.ErrorReporter
import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.domain.repository.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val transactionCategoryMapper: TransactionCategoryMapper,
    private val firestore: FirebaseFirestore,
    private val errorReporter: ErrorReporter,
): ICategoryRepository {
    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategory()
            .map { categoryEntities ->
                categoryEntities.map { categoryEntity ->
                    transactionCategoryMapper.mapCategoryEntityToDomain(categoryEntity)
                }
            }
    }

    override suspend fun getCategoryById(categoryId: String): Category? {
        val transactionEntity = categoryDao.getCategoryById(categoryId)
        return if (transactionEntity != null){
            transactionCategoryMapper.mapCategoryEntityToDomain(transactionEntity)
        } else {
            null
        }
    }

    override suspend fun syncCategoriesFromFirestore() {
        try {
            val snapshot = firestore.collection(CATEGORY_COLLECTION)
                .get()
                .await()

            val categories = snapshot.documents.mapNotNull { documentSnapshot ->
                documentSnapshot.toObject(TransactionCategoryFirestore::class.java)
            }.map { categoryFirestore ->
                transactionCategoryMapper.mapCategoryFirestoreToEntity(categoryFirestore)
            }

            if (categories.isNotEmpty()) {
                categoryDao.upsertCategories(categories)
            }

        } catch (e: Exception) {
            errorReporter.setCustomKey("sync_action", "sync_categories")
            errorReporter.log("Failed to sync admin categories: ${e.message}")
            errorReporter.recordException(e)
        }
    }
}