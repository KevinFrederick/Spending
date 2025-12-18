package com.kevinfreyap.data.mapper

import com.kevinfreyap.data.source.local.entity.DailyRatesEntity
import com.kevinfreyap.data.source.remote.firebase.ExchangeRatesFirestore
import com.kevinfreyap.data.source.remote.retrofit.response.ExchangeRatesResponse
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.ExchangeRates
import javax.inject.Inject

class ExchangeRatesMapper @Inject constructor() {
    // Remote Response
    fun mapRatesResponseToEntity(response: ExchangeRatesResponse): DailyRatesEntity {
        return DailyRatesEntity(
            dateKey = response.date,
            baseCurrency = response.baseCurrency,
            rates = response.rates,
        )
    }

    fun mapRatesResponseToFirestore(response: ExchangeRatesResponse): ExchangeRatesFirestore {
        return ExchangeRatesFirestore(
            dateKey = response.date,
            baseCurrency = response.baseCurrency,
            rates = response.rates
        )
    }

    fun mapRatesFirestoreToEntity(firestore: ExchangeRatesFirestore): DailyRatesEntity {
        return DailyRatesEntity(
            dateKey = firestore.dateKey,
            baseCurrency = firestore.baseCurrency,
            rates = firestore.rates
        )
    }
    
    // Local
    fun mapRatesEntityToDomain(entity: DailyRatesEntity): ExchangeRates {
        return ExchangeRates(
            dateKey = entity.dateKey,
            baseCurrency = AppCurrency.valueOf(entity.baseCurrency.uppercase()),
            rates = entity.rates.mapKeys { (key, _) ->
                AppCurrency.valueOf(key.uppercase())
            }
        )
    }
}