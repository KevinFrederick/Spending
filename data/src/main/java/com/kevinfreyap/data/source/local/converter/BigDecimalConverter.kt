package com.kevinfreyap.data.source.local.converter

import androidx.room.TypeConverter
import java.math.BigDecimal

class BigDecimalConverter {
    @TypeConverter
    fun fromBigDecimal(bigDecimal: BigDecimal?): String? {
        return bigDecimal?.toPlainString()
    }

    @TypeConverter
    fun toBigDecimal(text: String?): BigDecimal? {
        return text?.let { BigDecimal(it) }
    }
}