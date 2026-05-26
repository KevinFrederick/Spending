package com.kevinfreyap.domain.usecase.notification

import com.kevinfreyap.domain.model.NotificationPreferences
import com.kevinfreyap.domain.notification.NotificationScheduler
import com.kevinfreyap.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationInteractor @Inject constructor(
    private val userRepository: IUserRepository,
    private val notificationScheduler: NotificationScheduler
): NotificationUseCase {
    override fun getNotificationPref(): Flow<NotificationPreferences> = userRepository.getNotificationPref()

    override suspend fun setNotificationPref(notificationPref: NotificationPreferences) {
        userRepository.setNotificationPref(notificationPref)

        manageDailyReminder(notificationPref)
        manageMonthlySummary(notificationPref)
    }

    private fun manageDailyReminder(notificationPref: NotificationPreferences) {
        if (notificationPref.isDailyEnabled) {
            val hour = notificationPref.reminderHour.toIntOrNull() ?: 21
            val minute = notificationPref.reminderMinute.toIntOrNull() ?: 0

            notificationScheduler.scheduleDailyReminder(hour, minute)
        } else {
            notificationScheduler.cancelDailyReminder()
        }
    }

    private fun manageMonthlySummary(notificationPref: NotificationPreferences) {
        if (notificationPref.isMonthlyEnabled) {
            val hour = 21
            val minute = 0

            notificationScheduler.scheduleMonthlySummary(hour, minute)
        } else {
            notificationScheduler.cancelMonthlySummary()
        }
    }
}