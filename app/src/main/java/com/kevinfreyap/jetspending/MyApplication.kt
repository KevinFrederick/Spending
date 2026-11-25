package com.kevinfreyap.jetspending

import android.app.Application
import com.kevinfreyap.data.notification.NotificationChannelManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationChannelManager.createChannels(this)
    }
}