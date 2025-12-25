package com.kevinfreyap.data.mapper

import com.google.firebase.Timestamp
import com.kevinfreyap.data.source.local.model.PopulatedTransaction
import com.kevinfreyap.data.source.local.entity.TransactionEntity
import com.kevinfreyap.data.source.local.model.TransactionMath
import com.kevinfreyap.data.source.remote.firebase.TransactionFirestore
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.Transaction
import com.kevinfreyap.domain.model.TransactionMathWithRates
import com.kevinfreyap.domain.model.TransactionType
import com.kevinfreyap.domain.model.TransactionWithRates
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject

class TransactionMapper @Inject constructor(
    private val categoryMapper: TransactionCategoryMapper,
    private val exchangeRatesMapper: ExchangeRatesMapper,
) {
    // Local
    fun mapTransactionEntityToDomain(entity: PopulatedTransaction): Transaction {
        return Transaction(
            id = entity.transaction.id,
            name = entity.transaction.name,
            amount = entity.transaction.amount,
            currency = entity.transaction.currency,
            type = entity.transaction.type,
            category = categoryMapper.mapCategoryEntityToDomain(entity.category),
            date = entity.transaction.date,
            stringDate = entity.transaction.stringDate,
            notes = entity.transaction.notes,
            lastUpdated = entity.transaction.lastUpdated
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
            notes = domain.notes,
            lastUpdated = domain.lastUpdated,
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
            category = categoryMapper.mapCategoryEntityToDomain(entity.category),
            date = entity.date,
            stringDate = entity.stringDate,
            rates = entity.rate?.let { exchangeRatesMapper.mapRatesEntityToDomain(it) }
        )
    }

    // Remote
    fun mapTransactionDomainToFirestore(domain: Transaction): TransactionFirestore {
        return TransactionFirestore(
            id = domain.id,
            name = domain.name,
            amount = domain.amount.toPlainString(),
            currency = domain.currency.name,
            type = domain.type.name,
            categoryId = domain.category.id,
            date = Timestamp(domain.date.epochSecond, domain.date.nano),
            stringDate = domain.stringDate,
            notes = domain.notes,
            lastUpdated = domain.lastUpdated
        )
    }

    fun mapTransactionFirestoreToEntity(firestore: TransactionFirestore): TransactionEntity {
        return TransactionEntity(
            id = firestore.id,
            name = firestore.name,
            amount = firestore.amount.toBigDecimalOrNull() ?: BigDecimal.ZERO,
            currency = AppCurrency.valueOf(firestore.currency),
            type = TransactionType.valueOf(firestore.type),
            categoryId = firestore.categoryId,
            date = firestore.date?.let {
                Instant.ofEpochSecond(it.seconds, it.nanoseconds.toLong())
            } ?: Instant.now(),
            stringDate = firestore.stringDate,
            notes = firestore.notes,
            lastUpdated = firestore.lastUpdated
        )
    }
}