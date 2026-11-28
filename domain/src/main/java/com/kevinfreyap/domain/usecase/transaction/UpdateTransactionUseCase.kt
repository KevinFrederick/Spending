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
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val transactionRepository: ITransactionRepository,
    private val ensureRatesExist: EnsureRatesExistUseCase
) {
    suspend operator fun invoke(
        id: String,
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

        if (errors.isNotEmpty() || category == null) {
            return DomainResult.ValidationFailed(errors)
        }

        val transaction = Transaction(
            id = id,
            name = name,
            amount = amount,
            currency = currency,
            type = type,
            category = category,
            date = date,
            stringDate = stringDate,
            notes = notes,
            lastUpdated = System.currentTimeMillis()
        )

        ensureRatesExist(stringDate)

        return try {
            transactionRepository.updateTransaction(transaction)
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(e)
        }
    }
}