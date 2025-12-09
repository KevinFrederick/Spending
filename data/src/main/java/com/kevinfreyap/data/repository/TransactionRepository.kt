package com.kevinfreyap.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinfreyap.data.mapper.TransactionMapper
import com.kevinfreyap.data.source.local.dao.TransactionDao
import com.kevinfreyap.data.utils.DataConstants.TRANSACTION_COLLECTION
import com.kevinfreyap.data.utils.TransactionQuery
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionQuery: TransactionQuery,
    private val transactionMapper: TransactionMapper,
    private val firestore: FirebaseFirestore
): ITransactionRepository {
    override fun getTransactions(
        query: String,
        filter: TransactionFilter
    ): Flow<PagingData<Transaction>> {
        val filterQuery = transactionQuery.searchTransactionQuery(query, filter)

        val pager = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                transactionDao.getTransactions(filterQuery)
            }
        )
        return pager.flow
            .map { pagingData ->
                pagingData.map { transactionWithCategory ->
                    transactionMapper.mapTransactionEntityToDomain(transactionWithCategory)
                }
            }
    }

    override fun getLatestTransactions(limit: Int): Flow<List<Transaction>> {
        return transactionDao.getLatestTransactions(limit)
            .map { transactionWithCategories ->
                transactionWithCategories.map { transactionWithCategory ->
                    transactionMapper.mapTransactionEntityToDomain(transactionWithCategory)
                }
            }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        val entity = transactionMapper.mapTransactionDomainToEntity(transaction)
        transactionDao.insertTransaction(entity)
    }

    override suspend fun syncTransactionsFromFirestore() {

    }
}