package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.TransactionType

data class SummaryState(
    val transactionType: TransactionType,
    val value: String,
    val currency: String,
    val month: String,
)
