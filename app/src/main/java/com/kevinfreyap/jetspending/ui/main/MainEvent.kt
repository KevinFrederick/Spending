package com.kevinfreyap.jetspending.ui.main

import java.time.Instant

sealed interface MainEvent {
    data class SetUnlocked(val unlocked: Boolean): MainEvent
    data class RateMissing(val date: Instant): MainEvent
    data object AppBackgrounded: MainEvent
    data object EmergencyLogout: MainEvent
    data object DisableAppLockAndUnlock: MainEvent
    data object OpenWeeklySummaryReport: MainEvent
}