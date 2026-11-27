package com.kevinfreyap.data.source.local.query

import androidx.sqlite.db.SimpleSQLiteQuery
import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionFilter
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
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
                filter.customStartDate?.let { startDate ->
                    val startMillis = startDate.atZone(ZoneOffset.UTC).toLocalDate()
                        .atStartOfDay(zoneId)
                        .toInstant()
                        .toEpochMilli()

                    queryBuilder.append(" AND date >= ?")
                    args.add(startMillis)
                }

                filter.customEndDate?.let { endDate ->
                    val nextDayMillis = endDate.atZone(ZoneOffset.UTC).toLocalDate()
                        .plusDays(1)
                        .atStartOfDay(zoneId)
                        .toInstant()
                        .toEpochMilli()

                    queryBuilder.append(" AND date < ?")
                    args.add(nextDayMillis)
                }
            }
        }

        // Currency Filter
        filter.currency?.let {
            queryBuilder.append(" AND currency = ?")
            args.add(it.name)
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