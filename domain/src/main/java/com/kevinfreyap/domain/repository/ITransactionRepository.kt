package com.kevinfreyap.domain.repository

import androidx.paging.PagingData
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.model.TransactionMathWithRates
import com.kevinfreyap.domain.model.TransactionWithRates
import kotlinx.coroutines.flow.Flow
import java.time.Instant

interface ITransactionRepository {
    fun getTransactions(
        query: String,
        filter: TransactionFilter
    ): Flow<PagingData<TransactionWithRates>>

    fun getLatestTransactions(limit: Int): Flow<List<TransactionWithRates>>

    fun getTransactionById(transactionId: String): Flow<TransactionWithRates?>

    fun getDatesOfMissingRates(): Flow<List<String>>

    fun getAllTimeTransactions(): Flow<List<TransactionMathWithRates>>

    fun getTransactionsByTimeFrame(start: Instant, end: Instant): Flow<List<TransactionMathWithRates>>

    fun syncTransactionsFromFirestore(): Flow<Boolean>

    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transactionId: String)
}