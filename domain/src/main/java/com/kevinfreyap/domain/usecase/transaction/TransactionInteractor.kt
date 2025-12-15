package com.kevinfreyap.domain.usecase.transaction

import androidx.paging.PagingData
import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionFilter
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.repository.ICategoryRepository
import com.kevinfreyap.domain.repository.ITransactionRepository
import com.kevinfreyap.domain.resource.DomainResult
import kotlinx.coroutines.flow.Flow
import java.lang.Exception
import java.math.BigDecimal
import javax.inject.Inject
import java.time.Instant
import java.util.UUID

class TransactionInteractor @Inject constructor(
    private val transactionRepository: ITransactionRepository,
    private val categoryRepository: ICategoryRepository,
): TransactionUseCase {
    override val earliestTransactionYear: Int = 2020

    override fun getTransactions(
        query: String,
        filter: TransactionFilter
    ): Flow<PagingData<Transaction>> {
        return transactionRepository.getTransactions(query, filter)
    }

    override fun getLatestTransactions(): Flow<List<Transaction>> {
        return transactionRepository.getLatestTransactions(3)
    }

    override suspend fun insertTransaction(
        name: String,
        amount: BigDecimal,
        type: TransactionType,
        categoryId: String,
        date: Instant
    ): DomainResult<Unit> {
        val category = categoryRepository.getCategoryById(categoryId)

        val errors = validateTransaction(
            name = name,
            amount = amount,
            category = category,
        )

        if (errors.isNotEmpty()) {
            return DomainResult.ValidationFailed(errors)
        }

        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            name = name,
            amount = amount,
            currency = AppCurrency.IDR,
            type = type,
            category = category!!,
            date = date
        )

        return try {
            transactionRepository.insertTransaction(transaction)
            DomainResult.Success(Unit)
        } catch (e: Exception){
            DomainResult.Failure(e)
        }
    }

    override suspend fun syncTransactionsFromFirestore() {

    }

    private fun validateTransaction(
        name: String,
        amount: BigDecimal,
        category: Category?
    ): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (name.isBlank()) errors.add(ValidationError.TransactionNameRequired)
        if (amount <= BigDecimal.ZERO) errors.add(ValidationError.TransactionAmountInvalid)
        if (category == null) errors.add(ValidationError.TransactionCategoryMissing)

        return errors
    }
}