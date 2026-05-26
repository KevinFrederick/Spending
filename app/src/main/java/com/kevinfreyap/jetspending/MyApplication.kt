package com.kevinfreyap.jetspending

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.kevinfreyap.domain.notification.NotificationConst
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dailyChannel = NotificationChannel(
                NotificationConst.DAILY_CHANNEL_ID,
                NotificationConst.DAILY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = NotificationConst.DAILY_CHANNEL_DESC
            }

            val monthlyChannel = NotificationChannel(
                NotificationConst.MONTHLY_CHANNEL_ID,
                NotificationConst.MONTHLY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = NotificationConst.MONTHLY_CHANNEL_DESC
            }

            val notificationManager: NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannels(
                listOf(dailyChannel, monthlyChannel)
            )
        }
    }
}