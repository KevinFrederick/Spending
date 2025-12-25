package com.kevinfreyap.domain.usecase.transaction

import android.util.Log
import androidx.paging.PagingData
import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.domain.model.ChartData
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.model.SpendingIncomeStatus
import com.kevinfreyap.domain.model.TotalBalanceStatus
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.model.TransactionMathWithRates
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.model.TransactionWithRates
import com.kevinfreyap.domain.repository.ICategoryRepository
import com.kevinfreyap.domain.repository.IExchangeRatesRepository
import com.kevinfreyap.domain.repository.ITransactionRepository
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.currency.CurrencyUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.lang.Exception
import java.math.BigDecimal
import java.time.DayOfWeek
import javax.inject.Inject
import java.time.Instant
import java.time.Month
import java.time.ZoneId
import java.util.UUID

class TransactionInteractor @Inject constructor(
    private val transactionRepository: ITransactionRepository,
    private val categoryRepository: ICategoryRepository,
    private val exchangeRatesRepository: IExchangeRatesRepository,
    private val currencyUseCase: CurrencyUseCase
): TransactionUseCase {
    override val earliestTransactionYear: Int = 2020

    override fun getTransactions(
        query: String,
        filter: TransactionFilter
    ): Flow<PagingData<TransactionWithRates>> {
        return transactionRepository.getTransactions(query, filter)
    }

    override fun getLatestTransactions(): Flow<List<TransactionWithRates>> {
        return transactionRepository.getLatestTransactions(3)
    }

    override fun getTransactionById(transactionId: String): Flow<TransactionWithRates?> {
        return transactionRepository.getTransactionById(transactionId)
    }

    override fun getTotalBalance(selectedCurrency: AppCurrency): Flow<TotalBalanceStatus> {
        return transactionRepository.getAllTimeTransactions().map { transactions ->
            var total = BigDecimal.ZERO
            var missingRateCounts = 0

            transactions.forEach { transaction ->
                val convertedAmount = currencyUseCase.calculateAmountBasedOnRates(
                    amount = transaction.amount,
                    sourceCurrency = transaction.currency,
                    targetCurrency = selectedCurrency,
                    rates = transaction.rates
                )

                if (convertedAmount != null) {
                    if (transaction.type == TransactionType.INCOME) {
                        total += convertedAmount
                    } else {
                        total -= convertedAmount
                    }
                } else {
                    missingRateCounts++
                }
            }
            TotalBalanceStatus(
                totalBalance = total,
                isIncomplete = missingRateCounts > 0
            )
        }.flowOn(Dispatchers.Default)
    }

    override fun getStatsByTimeFrame(
        startDate: Instant,
        endDate: Instant,
        selectedCurrency: AppCurrency
    ): Flow<SpendingIncomeStatus> {
        return transactionRepository.getTransactionsByTimeFrame(startDate, endDate)
            .map { transactions ->
                var monthlyIncome = BigDecimal.ZERO
                var monthlySpending = BigDecimal.ZERO
                var missingRateCounts = 0

                transactions.forEach { transaction ->
                    val convertedAmount = currencyUseCase.calculateAmountBasedOnRates(
                        amount = transaction.amount,
                        sourceCurrency = transaction.currency,
                        targetCurrency = selectedCurrency,
                        rates = transaction.rates
                    )

                    if (convertedAmount != null) {
                        if (transaction.type == TransactionType.INCOME) {
                            monthlyIncome += convertedAmount
                        } else {
                            monthlySpending += convertedAmount
                        }
                    } else {
                        missingRateCounts++
                    }
                }

                SpendingIncomeStatus(
                    income = monthlyIncome,
                    spending = monthlySpending,
                    isIncomplete = missingRateCounts > 0
                )
            }
            .flowOn(Dispatchers.Default)
    }

    override fun getChartData(
        period: PeriodSelectorOption,
        startDate: Instant,
        endDate: Instant,
        selectedCurrency: AppCurrency
    ): Flow<List<ChartData>> {
        return transactionRepository.getTransactionsByTimeFrame(startDate, endDate)
            .map { transactions ->
                when(period) {
                    PeriodSelectorOption.WEEKLY -> groupedByDay(transactions, selectedCurrency)
                    PeriodSelectorOption.MONTHLY -> groupedByWeek(transactions, endDate, selectedCurrency)
                    PeriodSelectorOption.YEARLY -> groupedByMonth(transactions, selectedCurrency)
                }
            }
    }

    override fun syncTransactionsFromFirestore(): Flow<Boolean> {
        return transactionRepository.syncTransactionsFromFirestore()
    }

    override suspend fun insertTransaction(
        name: String,
        amount: BigDecimal,
        currency: AppCurrency,
        type: TransactionType,
        categoryId: String,
        date: Instant,
        stringDate: String,
        notes: String
    ): DomainResult<Unit> {
        val category = categoryRepository.getCategoryById(categoryId)

        val errors = validateTransaction(
            name = name,
            amount = amount,
            category = category,
            notes = notes
        )

        if (errors.isNotEmpty()) {
            return DomainResult.ValidationFailed(errors)
        }

        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            name = name,
            amount = amount,
            currency = currency,
            type = type,
            category = category!!,
            date = date,
            stringDate = stringDate,
            notes = notes,
            lastUpdated = System.currentTimeMillis()
        )

        ensureRatesExist(stringDate)

        return try {
            transactionRepository.insertTransaction(transaction)
            DomainResult.Success(Unit)
        } catch (e: Exception){
            DomainResult.Failure(e)
        }
    }

    override suspend fun updateTransaction(
        id: String,
        name: String,
        amount: BigDecimal,
        currency: AppCurrency,
        type: TransactionType,
        categoryId: String,
        date: Instant,
        stringDate: String,
        notes: String
    ): DomainResult<Unit> {
        val category = categoryRepository.getCategoryById(categoryId)

        val errors = validateTransaction(
            name = name,
            amount = amount,
            category = category,
            notes = notes
        )

        if (errors.isNotEmpty()) {
            return DomainResult.ValidationFailed(errors)
        }

        val transaction = Transaction(
            id = id,
            name = name,
            amount = amount,
            currency = currency,
            type = type,
            category = category!!,
            date = date,
            stringDate = stringDate,
            notes = notes,
            lastUpdated = System.currentTimeMillis()
        )

        ensureRatesExist(stringDate)

        return try {
            transactionRepository.updateTransaction(transaction)
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(e)
        }
    }

    override suspend fun deleteTransaction(transactionId: String) {
        transactionRepository.deleteTransaction(transactionId)
    }

    private val zoneId = ZoneId.systemDefault()

    private fun validateTransaction(
        name: String,
        amount: BigDecimal,
        category: Category?,
        notes: String
    ): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (name.isBlank()) errors.add(ValidationError.TransactionNameRequired)
        if (amount <= BigDecimal.ZERO) errors.add(ValidationError.TransactionAmountInvalid)
        if (category == null) errors.add(ValidationError.TransactionCategoryMissing)
        if (notes.length > 1000) errors.add(ValidationError.TransactionNotesTooLong)

        return errors
    }

    private suspend fun ensureRatesExist(stringDate: String) {
        try {
            exchangeRatesRepository.ensureRatesExist(stringDate)
        } catch (e: Exception) {
            Log.e("TransactionInteractor", e.message ?: "Something Wrong")
        }
    }

    private fun groupedByDay(list: List<TransactionMathWithRates>, selectedCurrency: AppCurrency): List<ChartData> {
        val groupedMap = list.groupingBy {
            it.date.atZone(zoneId).dayOfWeek
        }.fold(SpendingIncomeStatus()) { accumulator, transaction ->
            calculateAmount(accumulator, transaction, selectedCurrency)
        }

        return DayOfWeek.entries.mapIndexed { index,dayOfWeek ->
            ChartData(
                xLabel = dayOfWeek.name,
                amount = groupedMap[dayOfWeek] ?: SpendingIncomeStatus(),
                index = index // 0 for Mon, 6 for Sun
            )
        }
    }

    private fun groupedByWeek(list: List<TransactionMathWithRates>, endDate: Instant, selectedCurrency: AppCurrency): List<ChartData> {
        val groupedMap = list.groupingBy {
            val localDate = it.date.atZone(zoneId).toLocalDate()
            (localDate.dayOfMonth - 1) / 7
        }.fold(SpendingIncomeStatus()) {accumulator, transaction ->
            calculateAmount(accumulator, transaction, selectedCurrency)
        }

        val lastDayOfMonth = endDate.atZone(zoneId).dayOfMonth
        val maxIndex = (lastDayOfMonth - 1) / 7

        return (0..maxIndex).map { weekIndex ->
            val startDate = (weekIndex * 7) + 1
            val endDate = minOf((weekIndex + 1) * 7, lastDayOfMonth )

            ChartData(
                xLabel = "$startDate - $endDate",
                amount = groupedMap[weekIndex] ?: SpendingIncomeStatus(),
                index = weekIndex
            )
        }
    }

    private fun groupedByMonth(list: List<TransactionMathWithRates>, selectedCurrency: AppCurrency): List<ChartData> {
        val groupedMap = list.groupingBy {
            it.date.atZone(zoneId).month
        }.fold(SpendingIncomeStatus()) { accumulator, transaction ->
            calculateAmount(accumulator, transaction, selectedCurrency)
        }

        return Month.entries.mapIndexed { index, month ->
            ChartData(
                xLabel = month.name,
                amount = groupedMap[month] ?: SpendingIncomeStatus(),
                index = index
            )
        }
    }

    private fun calculateAmount(accumulator: SpendingIncomeStatus, transaction: TransactionMathWithRates, selectedCurrency: AppCurrency): SpendingIncomeStatus {
        val convertedAmount = currencyUseCase.calculateAmountBasedOnRates(
            amount = transaction.amount,
            sourceCurrency = transaction.currency,
            targetCurrency = selectedCurrency,
            rates = transaction.rates
        )

        return if (convertedAmount != null) {
            if (transaction.type == TransactionType.INCOME) {
                accumulator.copy(
                    income = accumulator.income + convertedAmount
                )
            } else {
                accumulator.copy(
                    spending = accumulator.spending + convertedAmount
                )
            }
        } else {
            accumulator.copy(
                isIncomplete = true
            )
        }
    }
}