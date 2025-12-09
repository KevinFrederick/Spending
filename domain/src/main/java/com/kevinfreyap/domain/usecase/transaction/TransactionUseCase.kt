package com.kevinfreyap.domain.usecase.transaction

import androidx.paging.PagingData
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionFilter
import kotlinx.coroutines.flow.Flow

interface TransactionUseCase {
    fun getTransactions(query: String, filter: TransactionFilter): Flow<PagingData<Transaction>>
    fun getLatestTransactions(): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun syncTransactionsFromFirestore()
}