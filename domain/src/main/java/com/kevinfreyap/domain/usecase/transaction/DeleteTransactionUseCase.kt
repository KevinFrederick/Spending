package com.kevinfreyap.domain.usecase.transaction

import com.kevinfreyap.domain.repository.ITransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository
) {
    suspend operator fun invoke(transactionId: String) = transactionRepository.deleteTransaction(transactionId)
}