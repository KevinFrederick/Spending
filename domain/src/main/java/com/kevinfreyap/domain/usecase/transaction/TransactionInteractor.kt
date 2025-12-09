package com.kevinfreyap.domain.usecase.transaction

import androidx.paging.PagingData
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionInteractor @Inject constructor(
    private val transactionRepository: ITransactionRepository
): TransactionUseCase {
    override fun getTransactions(
        query: String,
        filter: TransactionFilter
    ): Flow<PagingData<Transaction>> {
        return transactionRepository.getTransactions(query, filter)
    }

    override fun getLatestTransactions(): Flow<List<Transaction>> {
        return transactionRepository.getLatestTransactions(3)
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        transactionRepository.insertTransaction(transaction)
    }

    override suspend fun syncTransactionsFromFirestore() {

    }
}