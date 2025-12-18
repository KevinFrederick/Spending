package com.kevinfreyap.data.source.remote.retrofit.response

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.kevinfreyap.domain.model.AppCurrency
import java.lang.reflect.Type

class ExchangeRatesDeserializer: JsonDeserializer<ExchangeRatesResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ExchangeRatesResponse? {
        val jsonObject = json.asJsonObject

        val date = jsonObject.get("date").asString

        val baseCurrency = jsonObject.keySet().firstOrNull { it != "date" } ?: "unknown"

        val ratesObject = jsonObject.getAsJsonObject(baseCurrency)
        val ratesMap = mutableMapOf<String, Double>()
        val appCurrency = AppCurrency.entries.map { it.name }
        ratesObject.entrySet().forEach { (key, value) ->
            if (key.uppercase() in appCurrency) {
                ratesMap[key] = value.asDouble
            }
        }

        return ExchangeRatesResponse(
            date = date,
            baseCurrency = baseCurrency,
            rates = ratesMap
        )
    }
}