package com.kevinfreyap.domain.usecase.transaction

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.Category
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.repository.ITransactionRepository
import com.kevinfreyap.domain.resource.DomainResult
import com.kevinfreyap.domain.usecase.rates.EnsureRatesExistUseCase
import com.kevinfreyap.domain.usecase.validation.validateTransaction
import java.lang.Exception
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class InsertTransactionUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository,
    private val ensureRatesExist: EnsureRatesExistUseCase
) {
    suspend operator fun invoke(
        name: String,
        amount: BigDecimal,
        currency: AppCurrency,
        type: TransactionType,
        categoryId: String,
        date: Instant,
        stringDate: String,
        notes: String
    ): DomainResult<Unit> {
        val category = Category.fromId(categoryId)

        val errors = validateTransaction(
            name = name,
            amount = amount,
            category = category,
            notes = notes
        )

        if (errors.isNotEmpty()) {
            return DomainResult.ValidationFailed(errors)
        }

        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            name = name,
            amount = amount,
            currency = currency,
            type = type,
            category = category!!,
            date = date,
            stringDate = stringDate,
            notes = notes,
            lastUpdated = System.currentTimeMillis()
        )

        ensureRatesExist(stringDate)

        return try {
            transactionRepository.insertTransaction(transaction)
            DomainResult.Success(Unit)
        } catch (e: Exception){
            DomainResult.Failure(e)
        }
    }
}