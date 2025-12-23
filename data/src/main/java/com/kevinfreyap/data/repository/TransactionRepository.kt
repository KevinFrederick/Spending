package com.kevinfreyap.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kevinfreyap.data.mapper.TransactionMapper
import com.kevinfreyap.data.source.local.dao.TransactionDao
import com.kevinfreyap.data.source.local.entity.TransactionEntity
import com.kevinfreyap.data.source.remote.firebase.TransactionFirestore
import com.kevinfreyap.data.utils.DataConstants.TRANSACTION_COLLECTION
import com.kevinfreyap.data.utils.DataConstants.USER_COLLECTION
import com.kevinfreyap.data.utils.TransactionQuery
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.model.TransactionMathWithRates
import com.kevinfreyap.domain.model.TransactionWithRates
import com.kevinfreyap.domain.repository.ITransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionQuery: TransactionQuery,
    private val transactionMapper: TransactionMapper,
    private val firestore: FirebaseFirestore,
    private val fireAuth: FirebaseAuth
): ITransactionRepository {
    override fun getTransactions(
        query: String,
        filter: TransactionFilter
    ): Flow<PagingData<TransactionWithRates>> {
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
                pagingData.map { populatedTransaction ->
                    transactionMapper.mapTransactionEntityToDomainWithRates(populatedTransaction)
                }
            }
    }

    override fun getLatestTransactions(limit: Int): Flow<List<TransactionWithRates>> {
        return transactionDao.getLatestTransactions(limit)
            .map { populatedTransactions ->
                populatedTransactions.map { populatedTransaction ->
                    transactionMapper.mapTransactionEntityToDomainWithRates(populatedTransaction)
                }
            }
    }

    override fun getTransactionById(transactionId: String): Flow<TransactionWithRates?> {
        return transactionDao.getTransactionById(transactionId)
            .map { transaction ->
                transaction?.let {
                    transactionMapper.mapTransactionEntityToDomainWithRates(it)
                }
            }
    }

    override fun getDatesOfMissingRates(): Flow<List<String>> {
        return transactionDao.getDatesOfMissingRates()
    }

    override fun getAllTimeTransactions(): Flow<List<TransactionMathWithRates>> {
        return transactionDao.getAllTransactionsForBalance().map { transactionMaths ->
            transactionMaths.map { transactionMath ->
                transactionMapper.mapTransactionMathEntityToDomainWithRates(transactionMath)
            }
        }
    }

    override fun getTransactionsByTimeFrame(
        start: Instant,
        end: Instant
    ): Flow<List<TransactionMathWithRates>> {
        return transactionDao.getTransactionsByTimeFrame(start, end).map { transactionMaths ->
            transactionMaths.map { transactionMath ->
                transactionMapper.mapTransactionMathEntityToDomainWithRates(transactionMath)
            }
        }
    }

    override fun syncTransactionsFromFirestore(): Flow<Boolean> = callbackFlow {
        val currentUserId = fireAuth.currentUser?.uid

        if (currentUserId == null) {
            close()
            return@callbackFlow
        }

        val query = firestore.collection(USER_COLLECTION)
            .document(currentUserId)
            .collection(TRANSACTION_COLLECTION)

        val listenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(false)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Launch a coroutine within the flow's scope to do the DB write
                launch {
                    val toInsert = mutableListOf<TransactionEntity>()
                    val toDeleteIds = mutableListOf<String>()

                    for (change in snapshot.documentChanges) {
                        // Check metadata: Did this update come from THIS device?
                        // If hasPendingWrites is true, it means we just wrote it offline/locally.
                        // Skip saving to Room because Room already has it.
                        if (change.document.metadata.hasPendingWrites()) continue

                        when(change.type) {
                            DocumentChange.Type.ADDED,
                            DocumentChange.Type.MODIFIED -> {
                                val firestoreModel = change.document.toObject(TransactionFirestore::class.java)
                                toInsert.add(
                                    transactionMapper.mapTransactionFirestoreToEntity(firestoreModel)
                                )
                            }
                            DocumentChange.Type.REMOVED -> {
                                toDeleteIds.add(change.document.id)
                            }
                        }
                    }

                    if (toInsert.isNotEmpty()) transactionDao.insertAllTransaction(toInsert)
                    if (toDeleteIds.isNotEmpty()) transactionDao.deleteTransactionByIds(toDeleteIds)
                }
            }
        }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        val currentTimeStamp = System.currentTimeMillis()

        val entity = transactionMapper.mapTransactionDomainToEntity(transaction)
            .copy(lastUpdated = currentTimeStamp)
        transactionDao.insertTransaction(entity)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                insertTransactionToFirestore(transaction, currentTimeStamp)
            } catch (e: Exception) {
                Log.e(TAG + "Insert", e.message ?: "Something Wrong")
            }
        }
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        val currentTimeStamp = System.currentTimeMillis()

        val entity = transactionMapper.mapTransactionDomainToEntity(transaction)
            .copy(lastUpdated = currentTimeStamp)
        transactionDao.updateTransaction(entity)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                insertTransactionToFirestore(transaction, currentTimeStamp)
            } catch (e: Exception) {
                Log.e(TAG + "Update", e.message ?: "Something Wrong")
            }
        }
    }

    override suspend fun deleteTransaction(transactionId: String) {
        transactionDao.deleteTransactionById(transactionId)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                deleteTransactionFromFirestore(transactionId)
            } catch (e: Exception) {
                Log.e(TAG + "Delete", e.message ?: "Something Wrong")
            }
        }
    }

    private suspend fun insertTransactionToFirestore(transaction: Transaction, timeStamp: Long) {
        val currentUserId = fireAuth.currentUser?.uid ?: return

        val transactionFirestore = transactionMapper.mapTransactionDomainToFirestore(transaction)
            .copy(lastUpdated = timeStamp)

        firestore.collection(USER_COLLECTION)
            .document(currentUserId)
            .collection(TRANSACTION_COLLECTION)
            .document(transaction.id)
            .set(transactionFirestore, SetOptions.merge())
            .await()
    }

    private suspend fun deleteTransactionFromFirestore(transactionId: String) {
        val currentUserId = fireAuth.currentUser?.uid ?: return

        firestore.collection(USER_COLLECTION)
            .document(currentUserId)
            .collection(TRANSACTION_COLLECTION)
            .document(transactionId)
            .delete()
            .await()
    }

    companion object {
        private const val TAG = "TransactionRepo"
    }
}