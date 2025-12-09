package com.kevinfreyap.domain.model

data class TransactionFilter(
    val timeFilter: TimeFilterOption = TimeFilterOption.ALL_TIME,
    val customStartDate: Long? = null,
    val customEndDate: Long? = null,
    val fromAmount: Long? = null,
    val toAmount: Long? = null,
    val category: Category? = null
)
