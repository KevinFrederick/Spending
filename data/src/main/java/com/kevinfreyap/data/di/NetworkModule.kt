package com.kevinfreyap.data.di

import com.google.gson.GsonBuilder
import com.kevinfreyap.data.BuildConfig
import com.kevinfreyap.data.source.remote.retrofit.network.ApiService
import com.kevinfreyap.data.source.remote.retrofit.response.ExchangeRatesDeserializer
import com.kevinfreyap.data.source.remote.retrofit.response.ExchangeRatesResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): ApiService {
        val gson = GsonBuilder()
            .registerTypeAdapter(ExchangeRatesResponse::class.java, ExchangeRatesDeserializer())
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://placeholder.com/") // Dummy URL, override later
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}