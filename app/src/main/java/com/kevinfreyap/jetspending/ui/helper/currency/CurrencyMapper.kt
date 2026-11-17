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

fun String.cleanStringAmount(currency: AppCurrency): String? {
    var cleanAmount = this.replace(',', '.')

    if (currency == AppCurrency.IDR) {
        if (cleanAmount.contains('.')) return null
    }

    val decimalIndex = cleanAmount.indexOf('.')
    if (decimalIndex >= 0) {
        val decimalsAfterDot = cleanAmount.substring(decimalIndex + 1)
        if (decimalsAfterDot == "00") return null
        if (decimalsAfterDot.length > 2) return null
    }

    if (cleanAmount.count { it == '.' } > 1) return null
    cleanAmount = cleanAmount.filter { it.isDigit() || it == '.' }

    if (cleanAmount.isEmpty()) return ""

    val parts = cleanAmount.split('.')
    var intPart = parts[0].trimStart('0')
    if (intPart.isEmpty()) {
        intPart = "0"
    }

    return if (parts.size > 1) {
        "$intPart.${parts[1]}"
    } else {
        intPart
    }
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