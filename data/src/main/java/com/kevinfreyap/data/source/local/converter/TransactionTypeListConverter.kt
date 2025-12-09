package com.kevinfreyap.data.source.local.converter

import androidx.room.TypeConverter
import com.kevinfreyap.domain.model.TransactionType

class TransactionTypeListConverter {
    @TypeConverter
    fun fromTypesList(typeList: List<TransactionType>): String {
        return typeList.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toTypesList(text: String): List<TransactionType> {
        if (text.isEmpty()) return emptyList()
        return text.split(",").map {
            TransactionType.valueOf(it)
        }
    }
}