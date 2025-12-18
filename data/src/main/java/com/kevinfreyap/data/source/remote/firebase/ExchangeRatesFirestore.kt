package com.kevinfreyap.data.source.remote.firebase

import com.google.firebase.firestore.DocumentId

data class ExchangeRatesFirestore(
    @DocumentId
    val dateKey: String = "",
    val baseCurrency: String = "USD",
    val rates: Map<String, Double> = emptyMap()
)
