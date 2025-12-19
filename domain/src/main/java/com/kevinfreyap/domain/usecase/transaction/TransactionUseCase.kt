package com.kevinfreyap.domain.usecase.transaction

import androidx.paging.PagingData
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.MonthlyStatus
import com.kevinfreyap.domain.model.TotalBalanceStatus
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.model.TransactionWithRates
import com.kevinfreyap.domain.resource.DomainResult
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.Instant
import java.time.YearMonth

interface TransactionUseCase {
    val earliestTransactionYear: Int
    fun getTransactions(query: String, filter: TransactionFilter): Flow<PagingData<TransactionWithRates>>
    fun getLatestTransactions(): Flow<List<TransactionWithRates>>
    fun getTotalBalance(selectedCurrency: AppCurrency): Flow<TotalBalanceStatus>
    fun getMonthlyStats(month: YearMonth, selectedCurrency: AppCurrency): Flow<MonthlyStatus>

    suspend fun insertTransaction(
        name: String,
        amount: BigDecimal,
        currency: AppCurrency,
        type: TransactionType,
        categoryId: String,
        date: Instant,
        stringDate: String,
    ): DomainResult<Unit>
    suspend fun syncTransactionsFromFirestore()
}