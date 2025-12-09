package com.kevinfreyap.domain.repository

import androidx.paging.PagingData
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionFilter
import kotlinx.coroutines.flow.Flow

interface ITransactionRepository {
    fun getTransactions(
        query: String,
        filter: TransactionFilter
    ): Flow<PagingData<Transaction>>

    fun getLatestTransactions(limit: Int): Flow<List<Transaction>>

    suspend fun insertTransaction(transaction: Transaction)

    suspend fun syncTransactionsFromFirestore()
}