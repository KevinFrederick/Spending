package com.kevinfreyap.domain.usecase.reports

import com.kevinfreyap.domain.helper.aggregateTotals
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.domain.model.PeriodBucket
import com.kevinfreyap.domain.model.PeriodCategoryPercentage
import com.kevinfreyap.domain.model.PeriodReportData
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.model.TransactionMathConverted
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.repository.ITransactionRepository
import com.kevinfreyap.domain.usecase.currency.CalculateAmountBasedOnRatesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

class GetPeriodReportUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository,
    private val calculateAmount: CalculateAmountBasedOnRatesUseCase
) {
    operator fun invoke(
        startDate: Instant,
        endDate: Instant,
        period: PeriodSelectorOption,
        selectedCurrency: AppCurrency
    ): Flow<PeriodReportData> {
        return transactionRepository.getTransactionsByTimeFrame(
            start = startDate,
            end = endDate
        ).map { transactionMathWithRates ->
            val convertedTransactions = transactionMathWithRates.map { item ->
                TransactionMathConverted(
                    transactionMath = item,
                    convertedAmount = calculateAmount(
                        amount = item.amount,
                        sourceCurrency = item.currency,
                        targetCurrency = selectedCurrency,
                        rates = item.rates
                    )
                )
            }
            val totals = convertedTransactions.aggregateTotals()

            val buckets = buildPeriodBuckets(
                transactions = convertedTransactions,
                startDate = startDate,
                period = period,
            )

            val categories = buildCategoryPercentages(
                transactions = convertedTransactions,
                totalIncome = totals.income,
                totalExpense = totals.spending
            )

            PeriodReportData(
                totalExpense = totals.spending,
                totalIncome = totals.income,
                net = totals.netBalance,
                buckets = buckets,
                categories = categories,
                hasMissingRates = totals.isIncomplete
            )
        }
    }

    private fun buildCategoryPercentages(
        transactions: List<TransactionMathConverted>,
        totalIncome: BigDecimal,
        totalExpense: BigDecimal
    ): List<PeriodCategoryPercentage> {
        val categoryTotals = mutableMapOf<Pair<TransactionType, Category>, BigDecimal>()

        for ((transactionMath, convertedAmount) in transactions) {
            val converted = convertedAmount ?: continue
            val key = transactionMath.type to transactionMath.category

            val current = categoryTotals.getOrDefault(key, BigDecimal.ZERO)
            categoryTotals[key] = current.add(converted)
        }

        return categoryTotals.map { (key, groupTotal) ->
            val (type, category) = key
            val grandTotal = if (type == TransactionType.INCOME) totalIncome else totalExpense

            val percentage = if (grandTotal > BigDecimal.ZERO) {
                groupTotal.divide(grandTotal, 4, RoundingMode.HALF_UP).toFloat()
            } else {
                0f
            }

            PeriodCategoryPercentage(
                category = category,
                type = type,
                percentage = percentage,
                amount = groupTotal
            )
        }.sortedByDescending { it.percentage }
    }

    private fun buildPeriodBuckets(
        transactions: List<TransactionMathConverted>,
        startDate: Instant,
        period: PeriodSelectorOption,
    ): List<PeriodBucket> {
        val startLocal = startDate.atZone(ZoneId.systemDefault()).toLocalDate()

        // Build empty time slots so zero-transaction periods still render baseline bars
        // Pair<startDate, endDate> for each bar
        val slots: List<Pair<LocalDate, LocalDate>> = when(period) {
            // 7 bars
            PeriodSelectorOption.WEEKLY -> {
                (0L..6L).map { offset ->
                    val day = startLocal.plusDays(offset)
                    day to day
                }
            }
            // 4 bars
            PeriodSelectorOption.MONTHLY -> {
                val lastDayOfMonth = YearMonth.from(startLocal).lengthOfMonth()
                val ranges = mutableListOf<Pair<LocalDate, LocalDate>>()

                var dayStart = 1
                while (dayStart <= lastDayOfMonth) {
                    val dayEnd = minOf(dayStart + 6, lastDayOfMonth)
                    ranges.add(startLocal.withDayOfMonth(dayStart) to startLocal.withDayOfMonth(dayEnd))
                    dayStart += 7
                }
                ranges
            }
            // 12 bars
            PeriodSelectorOption.YEARLY -> {
                val year = startLocal.year
                (1..12).map { month ->
                    val yearMonth = YearMonth.of(year, month)
                    yearMonth.atDay(1) to yearMonth.atEndOfMonth()
                }
            }
        }

        val datedTransactions = transactions.map { transaction ->
            transaction.transactionMath.date.atZone(ZoneId.systemDefault()).toLocalDate() to transaction
        }

        return slots.map { (bucketStart, bucketEnd) ->
            var bucketExpense = BigDecimal.ZERO
            var bucketIncome = BigDecimal.ZERO

            for ((transactionDate, transaction) in datedTransactions) {
                if (!transactionDate.isBefore(bucketStart) && !transactionDate.isAfter(bucketEnd)) {
                    val converted = transaction.convertedAmount ?: continue

                    when(transaction.transactionMath.type) {
                        TransactionType.SPENDING -> {
                            bucketExpense = bucketExpense.add(converted)
                        }
                        TransactionType.INCOME -> {
                            bucketIncome = bucketIncome.add(converted)
                        }
                    }
                }

            }

            PeriodBucket(
                id = buildBucketId(period, bucketStart, bucketEnd),
                startDate = bucketStart,
                endDate = bucketEnd,
                expense = bucketExpense,
                income = bucketIncome
            )
        }
    }

    private fun buildBucketId(
        period: PeriodSelectorOption,
        start: LocalDate,
        end: LocalDate
    ): String = when (period) {
        PeriodSelectorOption.WEEKLY -> start.toString()                 // e.g., "2026-09-14"
        PeriodSelectorOption.MONTHLY -> "${start.dayOfMonth}-${end.dayOfMonth}" // e.g., "1-7", "8-14"
        PeriodSelectorOption.YEARLY -> YearMonth.from(start).toString() // e.g., "2026-09"
    }
}