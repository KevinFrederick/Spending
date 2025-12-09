package com.kevinfreyap.data.di

import com.kevinfreyap.data.repository.CategoryRepository
import com.kevinfreyap.data.repository.TransactionRepository
import com.kevinfreyap.domain.repository.ICategoryRepository
import com.kevinfreyap.domain.repository.ITransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideTransactionRepository(transactionRepository: TransactionRepository): ITransactionRepository

    @Binds
    @Singleton
    abstract fun provideCategoryRepository(categoryRepository: CategoryRepository): ICategoryRepository
}