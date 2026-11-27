package com.kevinfreyap.jetspending

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kevinfreyap.data.notification.NotificationChannelManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
        NotificationChannelManager.createChannels(this)
    }
}