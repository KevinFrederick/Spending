package com.kevinfreyap.jetspending.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity

fun Context.getFragmentActivity(): FragmentActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is FragmentActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}