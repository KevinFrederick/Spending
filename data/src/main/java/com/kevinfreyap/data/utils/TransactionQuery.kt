package com.kevinfreyap.data.utils

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
            "SELECT * FROM transactions WHERE name LIKE ?"
        )
        val args = mutableListOf<Any>("%$query%")

        // Time Filter
        when(filter.timeFilter){
            TimeFilterOption.ALL_TIME -> {}
            TimeFilterOption.LAST_7_DAYS -> {
                val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
                queryBuilder.append(" AND date >= ?")
                args.add(sevenDaysAgo)
            }
            TimeFilterOption.THIS_MONTH -> {
                val userTimeZone = ZoneId.systemDefault()
                val today = LocalDate.now(userTimeZone)

                val sevenDaysAgoDate = today.minusDays(7)

                val startOfDay = sevenDaysAgoDate.atStartOfDay(userTimeZone)

                val startMillis = startOfDay.toInstant().toEpochMilli()

                queryBuilder.append(" AND date >= ?")
                args.add(startMillis)
            }
            TimeFilterOption.PICK_DATE -> {
                val startDate = filter.customStartDate
                val endDate = filter.customEndDate

                if ((startDate != null) && (endDate == null)){
                    queryBuilder.append(" AND date >= ? AND date < ?")

                    val nextDay = startDate.plus(1, ChronoUnit.DAYS)

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
            args.add(filter.fromAmount.toPlainString())
        }

        if (filter.toAmount > BigDecimal.ZERO) {
            queryBuilder.append(" AND CAST(amount AS REAL) <= ?")
            args.add(filter.toAmount.toPlainString())
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