package com.kevinfreyap.domain.usecase.notification

import com.kevinfreyap.domain.model.NotificationPreferences
import kotlinx.coroutines.flow.Flow

interface NotificationUseCase {
    fun getNotificationPref(): Flow<NotificationPreferences>
    suspend fun setNotificationPref(notificationPref: NotificationPreferences)
}