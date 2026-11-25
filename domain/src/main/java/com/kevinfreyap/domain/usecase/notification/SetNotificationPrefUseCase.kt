package com.kevinfreyap.domain.usecase.notification

import com.kevinfreyap.domain.model.NotificationPreferences
import com.kevinfreyap.domain.notification.NotificationScheduler
import com.kevinfreyap.domain.repository.IUserRepository
import javax.inject.Inject

class SetNotificationPrefUseCase @Inject constructor(
    private val userRepository: IUserRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(notificationPref: NotificationPreferences) {
        userRepository.setNotificationPref(notificationPref)

        if (notificationPref.isMasterEnabled && notificationPref.isDailyEnabled) {
            notificationScheduler.scheduleDailyReminder(
                hour = notificationPref.reminderHour,
                minute = notificationPref.reminderMinute
            )
        } else {
            notificationScheduler.cancelDailyReminder()
        }

        if (notificationPref.isMasterEnabled && notificationPref.isWeeklyEnabled) {
            notificationScheduler.scheduleWeeklySummary(
                hour = 20,
                minute = 0
            )
        } else {
            notificationScheduler.cancelWeeklySummary()
        }
    }
}