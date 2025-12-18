package com.kevinfreyap.domain.repository

import androidx.paging.PagingData
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.model.TransactionWithRates
import kotlinx.coroutines.flow.Flow

interface ITransactionRepository {
    fun getTransactions(
        query: String,
        filter: TransactionFilter
    ): Flow<PagingData<TransactionWithRates>>

    fun getLatestTransactions(limit: Int): Flow<List<TransactionWithRates>>

    fun getDatesOfMissingRates(): Flow<List<String>>

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun syncTransactionsFromFirestore()
}