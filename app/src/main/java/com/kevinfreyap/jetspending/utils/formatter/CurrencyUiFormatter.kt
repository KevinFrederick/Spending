package com.kevinfreyap.jetspending.utils.formatter

import com.kevinfreyap.domain.model.AppCurrency
import java.lang.Exception
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.text.split

object CurrencyUiFormatter {
    fun getLocale(currencyCode: AppCurrency): Locale {
        return when(currencyCode) {
            AppCurrency.USD -> {
                Locale.US
            }
            AppCurrency.IDR -> {
                Locale.Builder()
                    .setLanguage("id")
                    .setRegion("ID")
                    .build()
            }
            AppCurrency.TWD -> {
                Locale.Builder()
                    .setLanguage("zh")
                    .setRegion("TW")
                    .build()
            }
            AppCurrency.MYR -> {
                Locale.Builder()
                    .setLanguage("ms")
                    .setRegion("MY")
                    .build()
            }
        }
    }

    fun getCurrency(currencyCode: AppCurrency): Currency {
        return Currency.getInstance(currencyCode.name)
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

    fun formatWithCode(amount: String, currencyCode: AppCurrency): String {
        val currencySymbol = when(currencyCode) {
            AppCurrency.USD -> "US$"
            AppCurrency.IDR -> "Rp"
            AppCurrency.TWD -> "NT$"
            AppCurrency.MYR -> "RM"
        }

        val formattedAmount = formatNumber(amount, currencyCode)

        return "$currencySymbol $formattedAmount"
    }
}