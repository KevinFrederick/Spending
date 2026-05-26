package com.kevinfreyap.domain.notification

interface NotificationScheduler {
    fun scheduleDailyReminder(hour: Int, minute: Int)
    fun scheduleMonthlySummary(hour: Int, minute: Int)
    fun cancelDailyReminder()
    fun cancelMonthlySummary()
}