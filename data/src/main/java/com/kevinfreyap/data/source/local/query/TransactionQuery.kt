package com.kevinfreyap.data.source.local.query

import androidx.sqlite.db.SimpleSQLiteQuery
import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionFilter
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class TransactionQuery @Inject constructor() {
    fun searchTransactionQuery(query: String, filter: TransactionFilter): SimpleSQLiteQuery{
        val queryBuilder = StringBuilder(
            "SELECT * FROM transactions WHERE 1=1"
        )
        val args = mutableListOf<Any>()

        if (query.isNotBlank()) {
            queryBuilder.append(" AND name LIKE ?")
            args.add("%${query.trim()}%")
        }

        // Time Filter
        val zoneId = ZoneId.systemDefault()
        when(filter.timeFilter){
            TimeFilterOption.ALL_TIME -> {}
            TimeFilterOption.LAST_7_DAYS -> {
                val sevenDaysAgoStart = LocalDate.now(zoneId)
                    .minusDays(7)
                    .atStartOfDay(zoneId)
                    .toInstant()
                    .toEpochMilli()

                queryBuilder.append(" AND date >= ?")
                args.add(sevenDaysAgoStart)
            }
            TimeFilterOption.THIS_MONTH -> {
                val today = LocalDate.now(zoneId)

                val startOfMonth = today.withDayOfMonth(1)
                    .atStartOfDay(zoneId)
                    .toInstant()
                    .toEpochMilli()

                val startOfNextMonth = today.withDayOfMonth(1)
                    .plusMonths(1)
                    .atStartOfDay(zoneId)
                    .toInstant()
                    .toEpochMilli() - 1L

                queryBuilder.append(" AND date >= ? AND date < ? ")
                args.add(startOfMonth)
                args.add(startOfNextMonth)
            }
            TimeFilterOption.PICK_DATE -> {
                val startDate = filter.customStartDate
                val endDate = filter.customEndDate

                if ((startDate != null) && (endDate == null)){
                    val nextDay = startDate.plus(1, ChronoUnit.DAYS)

                    queryBuilder.append(" AND date >= ? AND date < ?")

                    args.add(startDate.toEpochMilli())
                    args.add(nextDay.toEpochMilli())
                } else {
                    if (startDate != null) {
                        queryBuilder.append(" AND date >= ?")
                        args.add(startDate.toEpochMilli())
                    }

                    if (endDate != null) {
                        val nextDay = endDate.plus(1, ChronoUnit.DAYS)

                        queryBuilder.append(" AND date < ?")
                        args.add(nextDay.toEpochMilli())
                    }
                }
            }
        }

        // Amount Filter
        if (filter.fromAmount > BigDecimal.ZERO) {
            queryBuilder.append(" AND CAST(amount AS REAL) >= ?")
            args.add(filter.fromAmount.toDouble())
        }

        if (filter.toAmount > BigDecimal.ZERO) {
            queryBuilder.append(" AND CAST(amount AS REAL) <= ?")
            args.add(filter.toAmount.toDouble())
        }

        // Type Filter
        filter.type?.let {
            queryBuilder.append(" AND type = ?")
            args.add(it.name)
        }

        // Category filter
        filter.category?.let {
            queryBuilder.append(" AND categoryId = ?")
            args.add(it)
        }

        queryBuilder.append(" ORDER BY date DESC")

        return SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
    }
}