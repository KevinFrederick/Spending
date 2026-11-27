package com.kevinfreyap.data.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kevinfreyap.data.BuildConfig
import com.kevinfreyap.domain.error.ErrorReporter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashlyticsErrorReporter @Inject constructor(): ErrorReporter{
    private val crashlytics: FirebaseCrashlytics
        get() = FirebaseCrashlytics.getInstance()

    override fun log(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("AppLogger", message)
        }
        crashlytics.log(message)
    }

    override fun recordException(throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.e("AppLogger", throwable.message, throwable)
        }
        crashlytics.recordException(throwable)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }
}