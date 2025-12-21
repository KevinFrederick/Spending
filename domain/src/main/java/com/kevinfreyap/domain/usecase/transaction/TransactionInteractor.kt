package com.kevinfreyap.domain.usecase.transaction

import android.util.Log
import androidx.paging.PagingData
import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.domain.model.MonthlyStatus
import com.kevinfreyap.domain.model.TotalBalanceStatus
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionFilter
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
import javax.inject.Inject
import java.time.Instant
import java.time.LocalTime
import java.time.YearMonth
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

    override fun getMonthlyStats(
        month: YearMonth,
        selectedCurrency: AppCurrency
    ): Flow<MonthlyStatus> {
        val zoneId = ZoneId.systemDefault()
        val startOfMonth = month.atDay(1).atStartOfDay(zoneId).toInstant()
        val endOfMonth = month.atEndOfMonth().atTime(LocalTime.MAX).atZone(zoneId).toInstant()

        return transactionRepository.getTransactionsByTimeFrame(startOfMonth, endOfMonth)
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

                MonthlyStatus(
                    monthlyIncome = monthlyIncome,
                    monthlySpending = monthlySpending,
                    isIncomplete = missingRateCounts > 0
                )
            }
            .flowOn(Dispatchers.Default)
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
    ): DomainResult<Unit> {
        val category = categoryRepository.getCategoryById(categoryId)

        val errors = validateTransaction(
            name = name,
            amount = amount,
            category = category,
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
            lastUpdated = System.currentTimeMillis()
        )

        try {
            exchangeRatesRepository.ensureRatesExist(stringDate)
        } catch (e: Exception) {
            Log.e("TransactionInteractor", e.message ?: "Something Wrong")
        }

        return try {
            transactionRepository.insertTransaction(transaction)
            DomainResult.Success(Unit)
        } catch (e: Exception){
            DomainResult.Failure(e)
        }
    }

    private fun validateTransaction(
        name: String,
        amount: BigDecimal,
        category: Category?
    ): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (name.isBlank()) errors.add(ValidationError.TransactionNameRequired)
        if (amount <= BigDecimal.ZERO) errors.add(ValidationError.TransactionAmountInvalid)
        if (category == null) errors.add(ValidationError.TransactionCategoryMissing)

        return errors
    }
}