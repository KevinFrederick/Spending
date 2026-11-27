package com.kevinfreyap.domain.model

import java.time.DayOfWeek
import java.time.ZoneId
import java.time.Instant
import java.time.temporal.TemporalAdjusters

enum class PeriodSelectorOption {
    WEEKLY,
    MONTHLY,
    YEARLY;

    fun toInstantRange(
        reference: Instant = Instant.now(),
        zoneId: ZoneId = ZoneId.systemDefault(),
        firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
    ): InstantRange {
        val zonedDate = reference
            .atZone(zoneId)
            .toLocalDate()

        val startDate = when(this) {
            WEEKLY -> zonedDate.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
            MONTHLY -> zonedDate.withDayOfMonth(1)
            YEARLY -> zonedDate.withDayOfYear(1)
        }

        val endDate = when(this) {
            WEEKLY -> startDate.plusWeeks(1)
            MONTHLY -> startDate.plusMonths(1)
            YEARLY -> startDate.plusYears(1)
        }

        val startInstant = startDate
            .atStartOfDay(zoneId)
            .toInstant()

        val endInstant = endDate
            .atStartOfDay(zoneId)
            .toInstant()

        return InstantRange(
            start = startInstant,
            end = endInstant
        )
    }
}