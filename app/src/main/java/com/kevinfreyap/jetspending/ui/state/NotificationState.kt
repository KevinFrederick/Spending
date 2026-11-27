package com.kevinfreyap.jetspending.ui.state

data class NotificationState(
    val isMasterNotificationEnabled: Boolean = false,
    val isWeeklyNotificationEnabled: Boolean = true,
    val isDailyNotificationEnabled: Boolean = true,
    val isReminderDialogVisible: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0
)
