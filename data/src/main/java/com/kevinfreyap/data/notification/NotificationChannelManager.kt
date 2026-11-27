package com.kevinfreyap.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.kevinfreyap.domain.notification.NotificationConst

object NotificationChannelManager {
    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dailyChannel = NotificationChannel(
                NotificationConst.DAILY_CHANNEL_ID,
                NotificationConst.DAILY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = NotificationConst.DAILY_CHANNEL_DESC
            }

            val weeklyChannel = NotificationChannel(
                NotificationConst.WEEKLY_CHANNEL_ID,
                NotificationConst.WEEKLY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = NotificationConst.WEEKLY_CHANNEL_DESC
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannels(listOf(dailyChannel, weeklyChannel))
        }
    }
}