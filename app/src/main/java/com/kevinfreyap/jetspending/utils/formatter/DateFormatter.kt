package com.kevinfreyap.jetspending.utils.formatter

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalTime::class)
object DateFormatter {
    private val zoneId = ZoneId.systemDefault()
    fun formatToDate(instant: Instant): String{
        val dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
            .withZone(zoneId)
        return dateFormatter.format(instant)
    }

    fun formatToDateWithDay(instant: Instant): String {
        val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
        val dayFormatter = DateTimeFormatter.ofPattern("EEEE")

        val localDate = instant.atZone(zoneId).toLocalDate()
        val today = LocalDate.now(zoneId)

        return if (localDate.isEqual(today)) {
            "Today, ${dateFormatter.format(localDate)}"
        } else {
            "${dayFormatter.format(localDate)}, ${dateFormatter.format(localDate)}"
        }
    }

    fun formatToMonthYear(instant: Instant): String {
        val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
            .withZone(zoneId)
        return monthYearFormatter.format(instant)
    }

    fun formatToDailyRatesString(instant: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.of("UTC"))
        return formatter.format(instant)
    }

    fun formatYearMonthToString(yearMonth: YearMonth): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return formatter.format(yearMonth)
    }
}