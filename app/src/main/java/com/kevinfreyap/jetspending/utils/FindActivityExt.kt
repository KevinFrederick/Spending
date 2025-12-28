package com.kevinfreyap.jetspending.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

// Utility extension to find Activity from Compose Context
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}