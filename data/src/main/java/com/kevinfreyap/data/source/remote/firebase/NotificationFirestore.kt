package com.kevinfreyap.data.source.remote.firebase

data class NotificationFirestore(
    val monthlyNotification: Boolean = false,
    val dailyNotification: Boolean = false,
    val selectedHour: String = "21",
    val selectedMinute: String = "00",
    val lastUpdated: Long = System.currentTimeMillis()
)
