package com.kevinfreyap.jetspending.ui.helper.date

import androidx.annotation.StringRes
import com.kevinfreyap.jetspending.R

enum class DayGreeting (
    @StringRes val greetingRes: Int
) {
    MORNING (R.string.greeting_morning),
    AFTERNOON (R.string.greeting_afternoon),
    EVENING (R.string.greeting_evening);

    companion object {
        fun fromHour(hour: Int): DayGreeting =
            when(hour) {
                in 5..11 -> MORNING
                in 12 .. 17 -> AFTERNOON
                else -> EVENING
            }
    }
}