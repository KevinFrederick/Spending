package com.kevinfreyap.domain.model

import java.math.BigDecimal

data class TotalBalanceStatus(
    val totalBalance: BigDecimal,
    val isIncomplete: Boolean
)
