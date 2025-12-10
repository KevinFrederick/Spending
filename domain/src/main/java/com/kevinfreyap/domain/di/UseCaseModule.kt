package com.kevinfreyap.domain.di

import com.kevinfreyap.domain.usecase.category.CategoryInteractor
import com.kevinfreyap.domain.usecase.category.CategoryUseCase
import com.kevinfreyap.domain.usecase.transaction.TransactionInteractor
import com.kevinfreyap.domain.usecase.transaction.TransactionUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {
    @Binds
    @Singleton
    abstract fun provideTransactionUseCase(transactionInteractor: TransactionInteractor): TransactionUseCase

    @Binds
    @Singleton
    abstract fun provideCategoryUseCase(categoryInteractor: CategoryInteractor): CategoryUseCase

}