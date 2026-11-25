package com.kevinfreyap.data.mapper

import com.kevinfreyap.data.source.remote.firebase.NotificationFirestore
import com.kevinfreyap.domain.model.NotificationPreferences
import javax.inject.Inject

class NotificationPrefMapper @Inject constructor() {
    fun mapNotificationPreferenceToFirestore(notificationPref: NotificationPreferences): NotificationFirestore {
        return NotificationFirestore(
            masterNotification = notificationPref.isMasterEnabled,
            weeklyNotification = notificationPref.isWeeklyEnabled,
            dailyNotification = notificationPref.isDailyEnabled,
            selectedHour = notificationPref.reminderHour,
            selectedMinute = notificationPref.reminderMinute
        )
    }
}
