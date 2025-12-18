package com.kevinfreyap.data.source.remote.retrofit.response

data class ExchangeRatesResponse(
	val date: String,
	val baseCurrency: String,
	val rates: Map<String, Double>
)