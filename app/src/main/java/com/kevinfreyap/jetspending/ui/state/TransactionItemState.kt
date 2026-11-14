package com.kevinfreyap.jetspending.ui.state

import androidx.compose.ui.graphics.painter.Painter
import com.kevinfreyap.domain.model.TransactionType

data class TransactionItemState(
    val transactionId: String,
    val transactionType: TransactionType,
    val transactionCategoryIcon: Painter,
    val transactionName: String,
    val transactionDesc: String,
    val transactionAmount: String,
    val transactionCurrency: String
)
