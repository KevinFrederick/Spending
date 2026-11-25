package com.kevinfreyap.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kevinfreyap.domain.notification.NotificationScheduler
import com.kevinfreyap.domain.repository.IUserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver: BroadcastReceiver() {

    @Inject
    lateinit var userRepository: IUserRepository

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val prefs = userRepository.getNotificationPref().first()

                    if (prefs.isMasterEnabled && prefs.isDailyEnabled) {
                        val hour = prefs.reminderHour
                        val minute = prefs.reminderMinute
                        notificationScheduler.scheduleDailyReminder(hour, minute)
                    }

                    if (prefs.isMasterEnabled && prefs.isWeeklyEnabled) {
                        notificationScheduler.scheduleWeeklySummary(20, 0)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}