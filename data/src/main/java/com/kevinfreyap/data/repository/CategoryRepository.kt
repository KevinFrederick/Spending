package com.kevinfreyap.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinfreyap.data.mapper.TransactionCategoryMapper
import com.kevinfreyap.data.source.local.dao.CategoryDao
import com.kevinfreyap.data.source.remote.firebase.TransactionCategoryFirestore
import com.kevinfreyap.data.utils.DataConstants.CATEGORY_COLLECTION
import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.domain.repository.ICategoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val transactionCategoryMapper: TransactionCategoryMapper,
    private val firestore: FirebaseFirestore
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
        firestore.collection(CATEGORY_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    Log.e("CategoryRepository", error?.message ?: "Sync Category Error")
                    return@addSnapshotListener
                }

                val categories = snapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(TransactionCategoryFirestore::class.java)
                }.map { categoryFirestore ->
                    transactionCategoryMapper.mapCategoryFirestoreToEntity(categoryFirestore)
                }

                CoroutineScope(Dispatchers.IO).launch {
                    categoryDao.upsertCategories(categories)
                }
            }
    }
}