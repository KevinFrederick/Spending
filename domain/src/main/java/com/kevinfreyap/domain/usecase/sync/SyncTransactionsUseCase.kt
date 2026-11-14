package com.kevinfreyap.domain.usecase.sync

import com.kevinfreyap.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncTransactionsUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository
) {
    operator fun invoke(): Flow<Boolean> =
        transactionRepository.syncTransactionsFromFirestore()
}