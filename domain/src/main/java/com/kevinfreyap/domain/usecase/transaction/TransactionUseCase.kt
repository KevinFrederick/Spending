package com.kevinfreyap.domain.usecase.transaction

import androidx.paging.PagingData
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.resource.DomainResult
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.Instant

interface TransactionUseCase {
    val earliestTransactionYear: Int
    fun getTransactions(query: String, filter: TransactionFilter): Flow<PagingData<Transaction>>
    fun getLatestTransactions(): Flow<List<Transaction>>
    suspend fun insertTransaction(
        name: String,
        amount: BigDecimal,
        type: TransactionType,
        categoryId: String,
        date: Instant
    ): DomainResult<Unit>
    suspend fun syncTransactionsFromFirestore()
}