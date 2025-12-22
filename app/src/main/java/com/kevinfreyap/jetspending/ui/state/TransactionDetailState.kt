package com.kevinfreyap.jetspending.ui.state

import androidx.compose.ui.graphics.Color
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.jetspending.ui.model.CategoryUI
import java.math.BigDecimal
import java.time.Instant

data class TransactionDetailState(
    val transactionName: String = "",
    val transactionAmountDisplay: String = "",
    val transactionDateDisplay: String = "",
    val transactionType: TransactionType? = null,
    val transactionCategory: CategoryUI? = null,
    val transactionAmountRaw: BigDecimal? = null,
    val transactionDateRaw: Instant? = null,
    val transactionColor: Color = Color.Unspecified
)
