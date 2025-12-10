package com.kevinfreyap.data.utils

import androidx.sqlite.db.SimpleSQLiteQuery
import com.kevinfreyap.domain.model.TimeFilterOption
import com.kevinfreyap.domain.model.TransactionFilter
import java.util.Calendar
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
                val calendar = Calendar.getInstance()
                val start =calendar.apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                }.timeInMillis
                queryBuilder.append(" AND date >= ?")
                args.add(start)
            }
            TimeFilterOption.PICK_DATE -> {
                filter.customStartDate?.let {
                    queryBuilder.append(" AND date >= ?")
                    args.add(it)
                }

                filter.customEndDate?.let {
                    queryBuilder.append(" AND date <= ?")
                    args.add(it)
                }
            }
        }

        // Amount Filter
        filter.fromAmount?.let {
            queryBuilder.append(" AND amount >= ?")
            args.add(it)
        }
        filter.toAmount?.let {
            queryBuilder.append(" AND amount <= ?")
            args.add(it)
        }

        // Add category filter
        filter.category?.let {
            queryBuilder.append(" AND categoryId = ?")
            args.add(it.id)
        }

        queryBuilder.append(" ORDER BY date DESC")

        return SimpleSQLiteQuery(queryBuilder.toString(), args.toTypedArray())
    }
}