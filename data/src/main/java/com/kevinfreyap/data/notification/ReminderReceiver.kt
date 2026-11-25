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
import com.kevinfreyap.domain.notification.NotificationConst.IS_WEEKLY
import com.kevinfreyap.domain.notification.NotificationScheduler
import com.kevinfreyap.domain.repository.IUserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver: BroadcastReceiver() {
    @Inject
    lateinit var userRepository: IUserRepository

    @Inject
    lateinit var notificationScheduler: NotificationScheduler


    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val isWeekly = intent.getBooleanExtra(IS_WEEKLY, false)

        val title: String
        val desc: String
        val channelId: String
        val notificationId: Int

        if (isWeekly) {
            title = context.getString(R.string.notification_weekly_title)
            desc = context.getString(R.string.notification_weekly_desc)
            channelId = NotificationConst.WEEKLY_CHANNEL_ID
            notificationId = WEEKLY_NOTIFICATION_ID

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
            .setSmallIcon(R.drawable.app_icon_wallet)
            .setContentTitle(title)
            .setContentText(desc)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationId, notification)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = userRepository.getNotificationPref().first()

                if (isWeekly) {
                    if (prefs.isMasterEnabled && prefs.isWeeklyEnabled) {
                        notificationScheduler.scheduleWeeklySummary(20, 0)
                    }
                } else {
                    if (prefs.isMasterEnabled && prefs.isDailyEnabled) {
                        notificationScheduler.scheduleDailyReminder(prefs.reminderHour, prefs.reminderMinute)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val DAILY_NOTIFICATION_ID = 1001
        private const val WEEKLY_NOTIFICATION_ID = 1002
    }
}