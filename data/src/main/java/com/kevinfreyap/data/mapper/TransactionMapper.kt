package com.kevinfreyap.data.mapper

import com.kevinfreyap.data.source.local.entity.TransactionEntity
import com.kevinfreyap.data.source.local.entity.TransactionWithCategory
import com.kevinfreyap.domain.model.Transaction
import javax.inject.Inject

class TransactionMapper @Inject constructor(
    private val categoryMapper: TransactionCategoryMapper
) {
    fun mapTransactionEntityToDomain(entity: TransactionWithCategory): Transaction {
        return Transaction(
            id = entity.transaction.id,
            name = entity.transaction.name,
            amount = entity.transaction.amount,
            currency = entity.transaction.currency,
            type = entity.transaction.type,
            category = categoryMapper.mapCategoryEntityToDomain(entity.category),
            date = entity.transaction.date
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
            lastUpdated = System.currentTimeMillis()
        )
    }
}