package com.kevinfreyap.jetspending.ui.model

import androidx.compose.runtime.Immutable
import com.kevinfreyap.jetspending.ui.state.TransactionItemState

@Immutable
sealed interface TransactionsUI {
    @Immutable
    data class Header(val header: String): TransactionsUI
    @Immutable
    data class Item(val transaction: TransactionItemState): TransactionsUI
}
