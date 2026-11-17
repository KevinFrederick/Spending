package com.kevinfreyap.jetspending.ui.screen.transaction_form.state

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.ui.helper.date.toFullDateShortMonth
import java.time.Instant

data class AmountEntryState(
    val originalAmount: String = "",
    val originalCurrency: AppCurrency = AppCurrency.DEFAULT,
    val convertedAmount: String? = null,
    val convertedCurrency: AppCurrency = AppCurrency.DEFAULT,
    val conversionRate: String? = null,
    val rateDate: Instant = Instant.now()
) {
    val formattedRateDate: String
        get() = rateDate.toFullDateShortMonth()
}
