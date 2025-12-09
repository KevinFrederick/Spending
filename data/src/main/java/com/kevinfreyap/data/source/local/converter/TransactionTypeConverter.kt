package com.kevinfreyap.data.source.local.converter

import androidx.room.TypeConverter
import com.kevinfreyap.domain.model.TransactionType

class TransactionTypeConverter {
    @TypeConverter
    fun fromTransactionTypeObj(type: TransactionType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toTransactionTypeObj(typeName: String?): TransactionType? {
        return typeName?.let {
            TransactionType.valueOf(it)
        }
    }
}