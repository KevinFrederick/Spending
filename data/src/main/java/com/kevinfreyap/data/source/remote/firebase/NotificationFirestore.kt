package com.kevinfreyap.data.source.remote.firebase

data class NotificationFirestore(
    val masterNotification: Boolean = true,
    val weeklyNotification: Boolean = true,
    val dailyNotification: Boolean = true,
    val selectedHour: Int = 20,
    val selectedMinute: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
