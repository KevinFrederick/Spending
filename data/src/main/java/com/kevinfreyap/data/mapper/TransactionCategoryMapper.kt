package com.kevinfreyap.data.mapper

import com.kevinfreyap.data.source.local.entity.TransactionCategoryEntity
import com.kevinfreyap.data.source.remote.firebase.TransactionCategoryFirestore
import com.kevinfreyap.domain.model.Category
import javax.inject.Inject

class TransactionCategoryMapper @Inject constructor() {
    fun mapCategoryEntityToDomain(entity: TransactionCategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name,
            iconId = entity.iconId,
            sortOrder = entity.sortOrder,
            types = entity.types
        )
    }

    fun mapCategoryFirestoreToEntity(firestore: TransactionCategoryFirestore): TransactionCategoryEntity {
        return TransactionCategoryEntity(
            id = firestore.id,
            name = firestore.name,
            iconId = firestore.iconId,
            sortOrder = firestore.sortOrder,
            types = firestore.typesToObj()
        )
    }
}