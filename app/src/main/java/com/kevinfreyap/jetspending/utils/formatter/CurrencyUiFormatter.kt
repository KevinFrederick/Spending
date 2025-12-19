package com.kevinfreyap.jetspending.utils.formatter

import com.kevinfreyap.domain.model.AppCurrency
import java.lang.Exception
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import java.util.Locale.*
import kotlin.text.split

object CurrencyUiFormatter {
    fun getLocale(currencyCode: AppCurrency): Locale {
        return when(currencyCode) {
            AppCurrency.USD -> {
                US
            }
            AppCurrency.IDR -> {
                Builder()
                    .setLanguage("id")
                    .setRegion("ID")
                    .build()
            }
            AppCurrency.TWD -> {
                Builder()
                    .setLanguage("zh")
                    .setRegion("TW")
                    .build()
            }
            AppCurrency.MYR -> {
                Builder()
                    .setLanguage("ms")
                    .setRegion("MY")
                    .build()
            }
            AppCurrency.SGD -> {
                Builder()
                    .setLanguage("en")
                    .setRegion("SG")
                    .build()
            }
            AppCurrency.JPY -> {
                Builder()
                    .setLanguage("ja")
                    .setRegion("JP")
                    .build()
            }
        }
    }

    fun getNumberFormatter(currencyCode: AppCurrency): NumberFormat {
        val locale = getLocale(currencyCode)
        return (DecimalFormat.getNumberInstance(locale) as DecimalFormat).apply {
            isParseBigDecimal = true
            applyPattern("#,##0.##")
        }
    }

    fun formatNumber(raw: String, currencyCode: AppCurrency): String {
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

    fun trimFractionZero(amount: String): String {
        val parts = amount.split('.')
        val intPart = parts[0]
        val fractionPart = if (parts.size > 1) parts[1] else null

        return if (fractionPart != null) {
            "$intPart.$fractionPart"
        } else {
            intPart
        }
    }

    fun formatWithCode(amount: BigDecimal, currencyCode: AppCurrency): String {
        val fraction = if (currencyCode.isFraction) 2 else 0

        val currencySymbol = currencyCode.symbol
        val stringAmount = amount.setScale(fraction, RoundingMode.HALF_UP).toPlainString()

        val formattedAmount = formatNumber(
            stringAmount,
            currencyCode
        )

        return "$currencySymbol $formattedAmount"
    }

    fun cleanAmount(amount: String, currencyCode: AppCurrency): String? {
        var cleanAmount = amount.replace(',', '.')

        if (currencyCode == AppCurrency.IDR) {
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

        return cleanAmount
    }
}