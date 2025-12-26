package com.kevinfreyap.jetspending.utils.mapper

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.jetspending.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ThemeUiMapper @Inject constructor (
    @param:ApplicationContext private val context: Context
) {
    @StringRes
    fun mapThemeToNameRes(appTheme: AppTheme): Int {
        return when(appTheme) {
            AppTheme.LIGHT -> R.string.light_mode
            AppTheme.DARK -> R.string.dark_mode
            AppTheme.SYSTEM -> R.string.follow_system
        }
    }

    @DrawableRes
    fun mapThemeToIconRes(appTheme: AppTheme): Int {
        return when (appTheme) {
            AppTheme.LIGHT -> R.drawable.ic_light_mode_24
            AppTheme.DARK -> R.drawable.ic_nightlight_24
            AppTheme.SYSTEM -> if (isSystemDark()) R.drawable.ic_nightlight_24 else R.drawable.ic_light_mode_24
        }
    }

    private fun isSystemDark(): Boolean {
        val nightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightMode == Configuration.UI_MODE_NIGHT_YES
    }
}