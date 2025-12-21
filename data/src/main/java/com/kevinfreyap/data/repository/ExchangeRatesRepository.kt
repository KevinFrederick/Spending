package com.kevinfreyap.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinfreyap.data.BuildConfig
import com.kevinfreyap.data.mapper.ExchangeRatesMapper
import com.kevinfreyap.data.source.local.dao.ExchangeRatesDao
import com.kevinfreyap.data.source.remote.firebase.ExchangeRatesFirestore
import com.kevinfreyap.data.source.remote.retrofit.network.ApiService
import com.kevinfreyap.data.utils.DataConstants.BASE_CURRENCY_QUERY
import com.kevinfreyap.data.utils.DataConstants.EXCHANGE_RATES_COLLECTION
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.ExchangeRates
import com.kevinfreyap.domain.repository.IExchangeRatesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesRepository @Inject constructor(
    private val apiService: ApiService,
    private val exchangeRatesDao: ExchangeRatesDao,
    private val firestore: FirebaseFirestore,
    private val exchangeRatesMapper: ExchangeRatesMapper
): IExchangeRatesRepository {
    override fun getRatesFlow(dateKey: String): Flow<ExchangeRates?> {
        return exchangeRatesDao.getExchangeRatesByDate(dateKey).map { entity ->
            entity?.let { exchangeRatesMapper.mapRatesEntityToDomain(it) }
        }
    }

    override suspend fun ensureRatesExist(dateKey: String) = withContext(Dispatchers.IO) {
        val requiredCurrencies = AppCurrency.entries.map { it.name }

        // Check Room
        val localData = exchangeRatesDao.getExchangeRatesOneShot(dateKey)
        val isDataComplete = localData != null && requiredCurrencies.all { currencyCode ->
            localData.rates.keys.contains(currencyCode)
        }
        if (isDataComplete) return@withContext

        // Check Firestore
        try {
            val snapshot = firestore.collection(EXCHANGE_RATES_COLLECTION)
                .document(dateKey)
                .get()
                .await()

            if (snapshot.exists()) {
                val firestoreRates = snapshot.toObject(ExchangeRatesFirestore::class.java)
                if (firestoreRates != null) {
                    val ratesEntity = exchangeRatesMapper.mapRatesFirestoreToEntity(firestoreRates)
                    exchangeRatesDao.insertRates(ratesEntity)

                    val isDataComplete = requiredCurrencies.all {
                        firestoreRates.rates.containsKey(it)
                    }

                    if (isDataComplete) {
                        return@withContext
                    } else {
                        Log.e(TAG + "Check Firestore", "Firestore data incomplete")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG + "Check Firestore", e.message ?: "Something Wrong")
        }

        // Check API
        try {
            val baseUrl = String.format(BuildConfig.CURRENCY_URL, dateKey)
            val fullUrl = "${baseUrl}currencies/${BASE_CURRENCY_QUERY}.json"

            val apiResponse = apiService.getCurrencyExchange(fullUrl)

            val entity = exchangeRatesMapper.mapRatesResponseToEntity(apiResponse)
            val firestoreObj = exchangeRatesMapper.mapRatesResponseToFirestore(apiResponse)

            exchangeRatesDao.insertRates(entity)
            CoroutineScope(Dispatchers.IO).launch {
                firestore.collection(EXCHANGE_RATES_COLLECTION)
                    .document(dateKey)
                    .set(firestoreObj)
            }
        } catch (e: Exception) {
            Log.e(TAG + "Check API", e.message ?: "Something Wrong")
        }
    }

    override suspend fun syncDailyRates() {
        firestore.collection(EXCHANGE_RATES_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    Log.e(TAG, error?.message ?: "Sync Rates Error")
                    return@addSnapshotListener
                }

                val rates = snapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(ExchangeRatesFirestore::class.java)
                }.map { ratesFirestore ->
                    exchangeRatesMapper.mapRatesFirestoreToEntity(ratesFirestore)
                }

                CoroutineScope(Dispatchers.IO).launch {
                    exchangeRatesDao.upsertRates(rates)
                }
            }
    }

    companion object {
        private const val TAG = "ExchangeRatesRepository"
    }
}