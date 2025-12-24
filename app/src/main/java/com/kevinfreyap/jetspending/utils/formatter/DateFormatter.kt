package com.kevinfreyap.jetspending.utils.formatter

import com.kevinfreyap.domain.model.PeriodSelectorOption
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

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

    fun formatMonthToString(month: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return formatter.format(month)
    }

    fun formatInstantToDateHour(instant: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy | HH:mm")

        return formatter.format(instant.atZone(zoneId))
    }

    fun formatRangeInstant(period: PeriodSelectorOption, date: LocalDate): Pair<Instant, Instant> {
        val (startDate, endDate) = formatRange(period, date)

        val startInstant = startDate.atStartOfDay(zoneId).toInstant()
        val endInstant = endDate.atTime(LocalTime.MAX).atZone(zoneId).toInstant()

        return Pair(startInstant, endInstant)
    }

    fun formatRangeDisplay (period: PeriodSelectorOption, date: LocalDate): String {
        val (startDate, endDate) = formatRange(period, date)

        val dayFormatter = DateTimeFormatter.ofPattern("d")
        val monthFormatter = DateTimeFormatter.ofPattern("MMM")
        val yearFormatter = DateTimeFormatter.ofPattern("yyyy")

        return when(period) {
            PeriodSelectorOption.WEEKLY -> {
                if (startDate.month == endDate.month) {
                    "${startDate.format(dayFormatter)} - ${endDate.format(dayFormatter)} ${endDate.format(monthFormatter)} ${endDate.format(yearFormatter)}"
                } else {
                    "${startDate.format(dayFormatter)} ${startDate.format(monthFormatter)} - ${endDate.format(dayFormatter)} ${endDate.format(monthFormatter)} ${endDate.format(yearFormatter)}"
                }
            }
            PeriodSelectorOption.MONTHLY -> {
                "${date.format(monthFormatter)} ${date.format(yearFormatter)}"
            }
            PeriodSelectorOption.YEARLY -> {
                date.format(yearFormatter)
            }
        }
    }

    private fun formatRange(period: PeriodSelectorOption, date: LocalDate): Pair<LocalDate, LocalDate> {
        return when(period) {
            PeriodSelectorOption.WEEKLY -> {
                val start = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val end = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                Pair(start, end)
            }
            PeriodSelectorOption.MONTHLY -> {
                val start = date.with(TemporalAdjusters.firstDayOfMonth())
                val end = date.with(TemporalAdjusters.lastDayOfMonth())
                Pair(start, end)
            }
            PeriodSelectorOption.YEARLY -> {
                val start = date.with(TemporalAdjusters.firstDayOfYear())
                val end = date.with(TemporalAdjusters.lastDayOfYear())
                Pair(start, end)
            }
        }
    }
}