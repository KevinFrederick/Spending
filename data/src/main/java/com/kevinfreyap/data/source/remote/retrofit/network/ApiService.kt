package com.kevinfreyap.data.source.remote.retrofit.network

import com.kevinfreyap.data.source.remote.retrofit.response.ExchangeRatesResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getCurrencyExchange(
        @Url fullUrl: String
    ): ExchangeRatesResponse
}