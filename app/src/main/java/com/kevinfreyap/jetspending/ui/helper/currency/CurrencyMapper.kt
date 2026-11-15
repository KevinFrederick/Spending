package com.kevinfreyap.jetspending.ui.helper.currency

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.jetspending.utils.getLocale
import java.lang.Exception
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat

fun BigDecimal.toCurrencyString(currency: AppCurrency): String {
    val fraction = if (currency.isFraction) 2 else 0

    val stringAmount = this.setScale(fraction, RoundingMode.HALF_UP).toPlainString()

    val formattedAmount = formatNumber(
        stringAmount,
        currency
    )

    return formattedAmount
}

private fun formatNumber(raw: String, currencyCode: AppCurrency): String {
    val numberFormat = getNumberFormatter(currencyCode)

    val parts = raw.split('.')
    val intPart = parts[0]
    val fractionPart = if (parts.size > 1) parts[1] else null

    val formattedInt = try {
        if (intPart.isNotEmpty() && intPart != "-") {
            numberFormat.format(intPart.toBigInteger())
        } else {
            intPart
        }
    } catch (_: Exception) {
        intPart
    }

    val formatted = if (fractionPart != null && fractionPart.toDoubleOrNull() != 0.0) {
        "$formattedInt.$fractionPart"
    } else {
        formattedInt
    }

    return formatted
}

private fun getNumberFormatter(currencyCode: AppCurrency): NumberFormat {
    val locale = currencyCode.getLocale()
    return (DecimalFormat.getNumberInstance(locale) as DecimalFormat).apply {
        isParseBigDecimal = true
        applyPattern("#,##0.##")
    }
}