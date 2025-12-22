package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import java.time.Instant

data class TransactionState(
    val transactionName: String = "",
    val transactionAmountInput: String = "",
    val transactionAmountDisplay: String = "",
    val transactionType: TransactionType = TransactionType.SPENDING,
    val transactionCategories: List<CategoryUI> = emptyList(),
    val transactionCategoryId: String? = null,
    val transactionDate: Instant = Instant.now(),
    val transactionNotes: String = "",
    val transactionDateDisplay: String = ""
)
