package com.kevinfreyap.domain.model

data class NotificationPreferences(
    val isMasterEnabled: Boolean,
    val isWeeklyEnabled: Boolean,
    val isDailyEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int
)
