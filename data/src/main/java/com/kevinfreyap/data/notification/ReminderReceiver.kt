package com.kevinfreyap.data.notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kevinfreyap.data.R
import com.kevinfreyap.domain.notification.NotificationConst
import com.kevinfreyap.domain.notification.NotificationConst.IS_MONTHLY

class ReminderReceiver: BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val isMonthly = intent.getBooleanExtra(IS_MONTHLY, false)

        val title: String
        val desc: String
        val channelId: String
        val notificationId: Int

        if (isMonthly) {
            title = context.getString(R.string.notification_monthly_title)
            desc = context.getString(R.string.notification_monthly_desc)
            channelId = NotificationConst.MONTHLY_CHANNEL_ID
            notificationId = MONTHLY_NOTIFICATION_ID

        } else {
            title = context.getString(R.string.notification_daily_title)
            desc = context.getString(R.string.notification_daily_desc)
            channelId = NotificationConst.DAILY_CHANNEL_ID
            notificationId = DAILY_NOTIFICATION_ID
        }

        val tapIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        } ?: Intent()

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // The Notification
        val notification = NotificationCompat.Builder(
            context,
            channelId
        )
            .setSmallIcon(R.drawable.ic_notifications_active_24)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationId, notification)
    }

    companion object {
        private const val DAILY_NOTIFICATION_ID = 1001
        private const val MONTHLY_NOTIFICATION_ID = 1002
    }
}