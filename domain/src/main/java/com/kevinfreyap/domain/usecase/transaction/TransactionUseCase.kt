package com.kevinfreyap.domain.usecase.transaction

import androidx.paging.PagingData
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.CategoryPercentage
import com.kevinfreyap.domain.model.ChartData
import com.kevinfreyap.domain.model.PeriodSelectorOption
import com.kevinfreyap.domain.model.SpendingIncomeStatus
import com.kevinfreyap.domain.model.TotalBalanceStatus
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.model.TransactionWithRates
import com.kevinfreyap.domain.resource.DomainResult
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.Instant

interface TransactionUseCase {
    val earliestTransactionYear: Int
    fun getTransactions(query: String, filter: TransactionFilter): Flow<PagingData<TransactionWithRates>>
    fun getLatestTransactions(): Flow<List<TransactionWithRates>>
    fun getTransactionById(transactionId: String): Flow<TransactionWithRates?>
    fun getTotalBalance(selectedCurrency: AppCurrency): Flow<TotalBalanceStatus>
    fun getStatsByTimeFrame(
        startDate: Instant,
        endDate: Instant,
        selectedCurrency: AppCurrency
    ): Flow<SpendingIncomeStatus>
    fun getChartData(
        period: PeriodSelectorOption,
        startDate: Instant,
        endDate: Instant,
        selectedCurrency: AppCurrency
    ): Flow<List<ChartData>>
    fun getCategories(
        period: PeriodSelectorOption,
        startDate: Instant,
        endDate: Instant,
        selectedType: TransactionType,
        selectedCurrency: AppCurrency
    ): Flow<List<CategoryPercentage>>
    fun syncTransactionsFromFirestore(): Flow<Boolean>
    suspend fun insertTransaction(
        name: String,
        amount: BigDecimal,
        currency: AppCurrency,
        type: TransactionType,
        categoryId: String,
        date: Instant,
        stringDate: String,
        notes: String
    ): DomainResult<Unit>
    suspend fun updateTransaction(
        id: String,
        name: String,
        amount: BigDecimal,
        currency: AppCurrency,
        type: TransactionType,
        categoryId: String,
        date: Instant,
        stringDate: String,
        notes: String
    ): DomainResult<Unit>
    suspend fun deleteTransaction(transactionId: String)
}