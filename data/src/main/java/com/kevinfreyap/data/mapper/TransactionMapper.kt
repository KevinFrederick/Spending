package com.kevinfreyap.data.mapper

import com.kevinfreyap.data.source.local.model.PopulatedTransaction
import com.kevinfreyap.data.source.local.entity.TransactionEntity
import com.kevinfreyap.data.source.local.model.TransactionMath
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionMathWithRates
import com.kevinfreyap.domain.model.TransactionWithRates
import javax.inject.Inject

class TransactionMapper @Inject constructor(
    private val categoryMapper: TransactionCategoryMapper,
    private val exchangeRatesMapper: ExchangeRatesMapper,
) {
    fun mapTransactionEntityToDomain(entity: PopulatedTransaction): Transaction {
        return Transaction(
            id = entity.transaction.id,
            name = entity.transaction.name,
            amount = entity.transaction.amount,
            currency = entity.transaction.currency,
            type = entity.transaction.type,
            category = categoryMapper.mapCategoryEntityToDomain(entity.category),
            date = entity.transaction.date,
            stringDate = entity.transaction.stringDate
        )
    }

    fun mapTransactionDomainToEntity(domain: Transaction): TransactionEntity {
        return TransactionEntity(
            id = domain.id,
            name = domain.name,
            amount = domain.amount,
            currency = domain.currency,
            type = domain.type,
            categoryId = domain.category.id,
            date = domain.date,
            stringDate = domain.stringDate,
            lastUpdated = System.currentTimeMillis(),
        )
    }

    fun mapTransactionEntityToDomainWithRates(entity: PopulatedTransaction): TransactionWithRates {
        return TransactionWithRates(
            transaction = mapTransactionEntityToDomain(entity),
            rates = entity.rate?.let { exchangeRatesMapper.mapRatesEntityToDomain(it) }
        )
    }

    fun mapTransactionMathEntityToDomainWithRates(entity: TransactionMath): TransactionMathWithRates {
        return TransactionMathWithRates(
            amount = entity.amount,
            currency = entity.currency,
            type = entity.type,
            date = entity.date,
            stringDate = entity.stringDate,
            rates = entity.rate?.let { exchangeRatesMapper.mapRatesEntityToDomain(it) }
        )
    }
}