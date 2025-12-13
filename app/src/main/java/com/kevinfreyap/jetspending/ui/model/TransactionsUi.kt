package com.kevinfreyap.jetspending.ui.model

sealed class TransactionsUi {
    data class Header(val header: String): TransactionsUi()
    data class Item(val transaction: TransactionItemUi): TransactionsUi()
}
