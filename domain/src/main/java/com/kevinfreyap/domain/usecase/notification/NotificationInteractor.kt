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
        manageWeeklySummary(notificationPref)
    }

    private fun manageDailyReminder(notificationPref: NotificationPreferences) {
        if (notificationPref.isMasterEnabled && notificationPref.isDailyEnabled) {
            val hour = notificationPref.reminderHour
            val minute = notificationPref.reminderMinute

            notificationScheduler.scheduleDailyReminder(hour, minute)
        } else {
            notificationScheduler.cancelDailyReminder()
        }
    }

    private fun manageWeeklySummary(notificationPref: NotificationPreferences) {
        if (notificationPref.isMasterEnabled && notificationPref.isWeeklyEnabled) {
            val hour = 21
            val minute = 0

            notificationScheduler.scheduleWeeklySummary(hour, minute)
        } else {
            notificationScheduler.cancelWeeklySummary()
        }
    }
}