package com.kevinfreyap.data.di

import com.kevinfreyap.data.notification.NotificationSchedulerImpl
import com.kevinfreyap.domain.notification.NotificationScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    @Binds
    @Singleton
    abstract fun provideNotificationScheduler(notificationScheduler: NotificationSchedulerImpl): NotificationScheduler
}