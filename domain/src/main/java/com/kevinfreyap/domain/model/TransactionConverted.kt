package com.kevinfreyap.domain.model

import java.math.BigDecimal

data class TransactionConverted(
    val transaction: Transaction,
    val convertedAmount: BigDecimal?
)
