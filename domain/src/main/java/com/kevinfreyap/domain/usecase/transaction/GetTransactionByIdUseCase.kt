package com.kevinfreyap.domain.usecase.transaction

import com.kevinfreyap.domain.model.TransactionWithRates
import com.kevinfreyap.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionByIdUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository
) {
    operator fun invoke(transactionId: String): Flow<TransactionWithRates?> =
        transactionRepository.getTransactionById(transactionId)
}