package com.kevinfreyap.domain.model

import java.math.BigDecimal

data class TransactionMathConverted(
    val transactionMath: TransactionMathWithRates,
    val convertedAmount: BigDecimal?
)
