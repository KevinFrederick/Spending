package com.kevinfreyap.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kevinfreyap.data.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver: BroadcastReceiver() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var notificationSchedulerImpl: NotificationSchedulerImpl

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val prefs = userRepository.getNotificationPref().first()

                    if (prefs.isDailyEnabled) {
                        val hour = prefs.reminderHour.toIntOrNull() ?: 21
                        val minute = prefs.reminderMinute.toIntOrNull() ?: 0
                        notificationSchedulerImpl.scheduleDailyReminder(hour, minute)
                    }

                    if (prefs.isMonthlyEnabled) {
                        val hour = 21
                        val minute = 0
                        notificationSchedulerImpl.scheduleMonthlySummary(hour, minute)
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