package com.kevinfreyap.data.source.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ExchangeRatesConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromMap(map: Map<String, Double>): String {
        return gson.toJson(map)
    }

    @TypeConverter
    fun toMap(json: String): Map<String, Double> {
        val mapType = object : TypeToken<Map<String, Double>>() {}.type
        return gson.fromJson(json, mapType)
    }
}