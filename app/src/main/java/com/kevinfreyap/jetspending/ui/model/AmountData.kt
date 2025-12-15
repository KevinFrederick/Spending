package com.kevinfreyap.jetspending.ui.model

import java.math.BigDecimal

data class AmountData(
    val fromInput: String,
    val toInput: String,
    val fromRaw: BigDecimal,
    val toRaw: BigDecimal
)
