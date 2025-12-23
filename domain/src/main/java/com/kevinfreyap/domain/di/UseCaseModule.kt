package com.kevinfreyap.domain.di

import com.kevinfreyap.domain.usecase.authentication.AuthenticationInteractor
import com.kevinfreyap.domain.usecase.authentication.AuthenticationUseCase
import com.kevinfreyap.domain.usecase.category.CategoryInteractor
import com.kevinfreyap.domain.usecase.category.CategoryUseCase
import com.kevinfreyap.domain.usecase.connectivity.ConnectivityInteractor
import com.kevinfreyap.domain.usecase.connectivity.ConnectivityUseCase
import com.kevinfreyap.domain.usecase.currency.CurrencyInteractor
import com.kevinfreyap.domain.usecase.currency.CurrencyUseCase
import com.kevinfreyap.domain.usecase.rates.ExchangeRatesInteractor
import com.kevinfreyap.domain.usecase.rates.ExchangeRatesUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionInteractor
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import com.kevinfreyap.domain.usecase.user.UserInteractor
import com.kevinfreyap.domain.usecase.user.UserUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Binds
    @Singleton
    abstract fun provideAuthenticationUseCase(authenticationInteractor: AuthenticationInteractor): AuthenticationUseCase

    @Binds
    @Singleton
    abstract fun provideTransactionUseCase(transactionInteractor: TransactionInteractor): TransactionUseCase

    @Binds
    @Singleton
    abstract fun provideCategoryUseCase(categoryInteractor: CategoryInteractor): CategoryUseCase

    @Binds
    @Singleton
    abstract fun provideUserUseCase(userInteractor: UserInteractor): UserUseCase

    @Binds
    @Singleton
    abstract fun provideExchangeRatesUseCae(exchangeRatesInteractor: ExchangeRatesInteractor): ExchangeRatesUseCase

    @Binds
    @Singleton
    abstract fun provideCurrencyUseCase(currencyInteractor: CurrencyInteractor): CurrencyUseCase

    @Binds
    @Singleton
    abstract fun provideConnectivityUseCase(connectivityInteractor: ConnectivityInteractor): ConnectivityUseCase
}