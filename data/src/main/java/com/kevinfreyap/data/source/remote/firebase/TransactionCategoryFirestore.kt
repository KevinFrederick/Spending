package com.kevinfreyap.data.source.remote.firebase

import com.google.firebase.firestore.DocumentId
import com.kevinfreyap.domain.model.TransactionType

data class TransactionCategoryFirestore(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val iconId: String = "",
    val sortOrder: Int = 0,
    val type: List<String> = emptyList()
) {
    fun typesToObj(): List<TransactionType> {
        return type.map { TransactionType.valueOf(it) }
    }
}
