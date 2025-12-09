package com.kevinfreyap.data.source.local.converter

import androidx.room.TypeConverter
import com.kevinfreyap.domain.model.AppCurrency

class CurrencyConverter {
    @TypeConverter
    fun fromCurrencyObj(currency: AppCurrency?): String? {
        return currency?.name
    }

    @TypeConverter
    fun toCurrencyObj(currencyName: String?): AppCurrency? {
        return currencyName?.let {
            AppCurrency.valueOf(it)
        }
    }
}