package com.kevinfreyap.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kevinfreyap.domain.notification.NotificationConst
import com.kevinfreyap.domain.notification.NotificationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class NotificationSchedulerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
): NotificationScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleDailyReminder(hour: Int, minute: Int) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_REMINDER_REQ_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        var target = ZonedDateTime.now()
            .withHour(hour)
            .withMinute(minute)
            .withSecond(0)
            .withNano(0)

        if (!target.isAfter(ZonedDateTime.now())) {
            target = target.plusDays(1)
        }

        scheduleAlarm(target.toInstant().toEpochMilli(), pendingIntent)
    }

    override fun scheduleWeeklySummary(hour: Int, minute: Int) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(NotificationConst.IS_WEEKLY, true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            WEEKLY_SUMMARY_REQ_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        var target = ZonedDateTime.now()
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            .withHour(hour)
            .withMinute(minute)
            .withSecond(0)
            .withNano(0)

        if (!target.isAfter(ZonedDateTime.now())) {
            target = target.plusWeeks(1)
        }

        scheduleAlarm(target.toInstant().toEpochMilli(), pendingIntent)
    }

    private fun scheduleAlarm(triggerAtMillis: Long, pendingIntent: PendingIntent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    override fun cancelDailyReminder() {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_REMINDER_REQ_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    override fun cancelWeeklySummary() {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(NotificationConst.IS_WEEKLY, true)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            WEEKLY_SUMMARY_REQ_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        private const val DAILY_REMINDER_REQ_CODE = 100
        private const val WEEKLY_SUMMARY_REQ_CODE = 101
    }
}
