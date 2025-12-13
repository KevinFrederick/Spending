package com.kevinfreyap.jetspending.ui.model

import androidx.compose.ui.graphics.Color
import java.time.Instant

data class TransactionItemUi(
    val transactionId: String,
    val transactionName: String,
    val transactionTypeBackground: Color,
    val transactionCategoryIcon: Int,
    val transactionAmount: String,
    val transactionDate: String,
    val transactionDateRaw: Instant
)
