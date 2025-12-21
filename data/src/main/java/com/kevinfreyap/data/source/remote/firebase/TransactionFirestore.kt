package com.kevinfreyap.data.source.remote.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class TransactionFirestore(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val amount: String = "0",
    val currency: String = "",
    val type: String = "",
    val categoryId: String = "",
    val date: Timestamp? = null,
    val stringDate: String = "",
    val lastUpdated: Long = 0L
)
