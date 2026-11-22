package com.kevinfreyap.jetspending.ui.helper.period

import com.kevinfreyap.domain.model.PeriodSelectorOption
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

fun PeriodSelectorOption.toInstantRange(
    anchorDate: LocalDate,
    zoneId: ZoneId = ZoneId.systemDefault()
): Pair<Instant, Instant> {
    val startLocal: LocalDate
    val endExclusiveLocal: LocalDate

    when(this) {
        PeriodSelectorOption.WEEKLY -> {
            startLocal = anchorDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            endExclusiveLocal = startLocal.plusWeeks(1)
        }
        PeriodSelectorOption.MONTHLY -> {
            startLocal = anchorDate.withDayOfMonth(1)
            endExclusiveLocal = startLocal.plusMonths(1)
        }
        PeriodSelectorOption.YEARLY -> {
            startLocal = anchorDate.withDayOfYear(1)
            endExclusiveLocal = startLocal.plusYears(1)
        }
    }

    return startLocal.atStartOfDay(zoneId).toInstant() to
            endExclusiveLocal.atStartOfDay(zoneId).toInstant()
}

fun PeriodSelectorOption.step(
    anchorDate: LocalDate,
    amount: Long, // 1 -> Next | -1 -> Previous
): LocalDate =
    when(this) {
        PeriodSelectorOption.WEEKLY -> anchorDate.plusWeeks(amount)
        PeriodSelectorOption.MONTHLY -> anchorDate.plusMonths(amount)
        PeriodSelectorOption.YEARLY -> anchorDate.plusYears(amount)
    }

fun PeriodSelectorOption.isAfterCurrentPeriod(
    candidate: LocalDate, // Next week, next month, next year
    today: LocalDate
): Boolean =
    when(this) {
        PeriodSelectorOption.WEEKLY -> {
            candidate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)) >
                today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        }
        PeriodSelectorOption.MONTHLY -> {
            YearMonth.from(candidate) > YearMonth.from(today)
        }
        PeriodSelectorOption.YEARLY -> {
            candidate.year > today.year
        }
    }

fun PeriodSelectorOption.formatHeaderLabel(
    anchorDate: LocalDate,
    locale: Locale = Locale.getDefault()
): String =
    when(this) {
        PeriodSelectorOption.WEEKLY -> {
            val start = anchorDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val end = start.plusDays(6)

            val dayFormatter = DateTimeFormatter.ofPattern("d", locale)
            val dayMonthFormatter = DateTimeFormatter.ofPattern("d MMM", locale)
            val fullFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", locale)

             when {
                 start.year != end.year ->
                     "${start.format(fullFormatter)} - ${end.format(fullFormatter)}"

                 start.month != end.month ->
                     "${start.format(dayMonthFormatter)} - ${end.format(fullFormatter)}"

                 else ->
                     "${start.format(dayFormatter)} - ${end.format(fullFormatter)}"
             }
        }
        PeriodSelectorOption.MONTHLY -> {
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
            anchorDate.format(formatter)
        }
        PeriodSelectorOption.YEARLY -> {
            anchorDate.year.toString()
        }
    }