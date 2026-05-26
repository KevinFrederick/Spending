package com.kevinfreyap.data.di

import com.kevinfreyap.data.repository.AuthenticationRepository
import com.kevinfreyap.data.repository.CategoryRepository
import com.kevinfreyap.data.repository.ConnectivityRepository
import com.kevinfreyap.data.repository.ExchangeRatesRepository
import com.kevinfreyap.data.repository.PrivacySecurityRepository
import com.kevinfreyap.data.repository.TransactionRepository
import com.kevinfreyap.data.repository.UserRepository
import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.repository.ICategoryRepository
import com.kevinfreyap.domain.repository.IConnectivityRepository
import com.kevinfreyap.domain.repository.IExchangeRatesRepository
import com.kevinfreyap.domain.repository.IPrivacySecurityRepository
import com.kevinfreyap.domain.repository.ITransactionRepository
import com.kevinfreyap.domain.repository.IUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideAuthenticationRepository(authenticationRepository: AuthenticationRepository): IAuthenticationRepository

    @Binds
    @Singleton
    abstract fun provideTransactionRepository(transactionRepository: TransactionRepository): ITransactionRepository

    @Binds
    @Singleton
    abstract fun provideCategoryRepository(categoryRepository: CategoryRepository): ICategoryRepository

    @Binds
    @Singleton
    abstract fun provideUserRepository(userRepository: UserRepository): IUserRepository

    @Binds
    @Singleton
    abstract fun provideExchangeRatesRepository(exchangeRatesRepository: ExchangeRatesRepository): IExchangeRatesRepository

    @Binds
    @Singleton
    abstract fun provideConnectivityRepository(connectivityRepository: ConnectivityRepository): IConnectivityRepository

    @Binds
    @Singleton
    abstract fun providePrivacySecurityRepository(privacySecurityRepository: PrivacySecurityRepository): IPrivacySecurityRepository
}