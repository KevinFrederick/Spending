package com.kevinfreyap.domain.model

data class NotificationPreferences(
    val isMonthlyEnabled: Boolean = false,
    val isDailyEnabled: Boolean = false,
    val reminderHour: String = "21",
    val reminderMinute: String = "00"
)
