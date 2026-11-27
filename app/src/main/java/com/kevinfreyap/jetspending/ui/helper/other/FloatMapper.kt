package com.kevinfreyap.jetspending.ui.helper.other

import java.text.NumberFormat
import java.util.Locale

fun Float.toLocalePercentageText(
    maxFractionDigits: Int = 1,
    locale: Locale = Locale.getDefault()
): String {
    val clamped = this.coerceIn(0f, 1f)
    if (clamped > 0f && clamped < 0.001f) return "<0.1%"

    val formatter = NumberFormat.getPercentInstance(locale).apply {
        maximumFractionDigits = maxFractionDigits
        minimumFractionDigits = 0
    }
    return formatter.format(clamped)
}