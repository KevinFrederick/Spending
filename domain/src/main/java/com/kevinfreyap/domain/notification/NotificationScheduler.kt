package com.kevinfreyap.domain.notification

interface NotificationScheduler {
    fun scheduleDailyReminder(hour: Int, minute: Int)
    fun scheduleWeeklySummary(hour: Int, minute: Int)
    fun cancelDailyReminder()
    fun cancelWeeklySummary()
}