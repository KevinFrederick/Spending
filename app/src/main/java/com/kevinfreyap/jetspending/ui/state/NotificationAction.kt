package com.kevinfreyap.jetspending.ui.state

interface NotificationAction {
    fun onSwitchDailyNotification(isChecked: Boolean)
    fun onSwitchMonthlyNotification(isChecked: Boolean)
    fun onTimeChange(time: String)
}