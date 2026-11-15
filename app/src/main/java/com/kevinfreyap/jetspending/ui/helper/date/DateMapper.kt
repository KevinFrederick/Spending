package com.kevinfreyap.jetspending.ui.helper.date

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Instant.toFullMonthYear(
    zoneId: ZoneId = ZoneId.systemDefault(),
    locale: Locale = Locale.getDefault()
): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM, yyyy", locale)
    return this.atZone(zoneId).format(formatter)
}

fun Instant.toFullDate(
    zoneId: ZoneId = ZoneId.systemDefault(),
    locale: Locale = Locale.getDefault()
): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", locale)
    return this.atZone(zoneId).format(formatter)
}

fun Instant.toMonth(
    zoneId: ZoneId = ZoneId.systemDefault(),
    locale: Locale = Locale.getDefault()
): String {
    val formatter = DateTimeFormatter.ofPattern("MMMM", locale)
    return this.atZone(zoneId).format(formatter)
}