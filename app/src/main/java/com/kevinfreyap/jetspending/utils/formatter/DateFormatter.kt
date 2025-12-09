package com.kevinfreyap.jetspending.utils.formatter

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import java.time.Instant
import java.time.LocalDate

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
}