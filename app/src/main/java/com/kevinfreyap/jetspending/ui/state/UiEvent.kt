package com.kevinfreyap.jetspending.ui.state

import android.content.Context
import androidx.annotation.StringRes

sealed interface UiEvent<out T> {
    data class Navigate <T> (val destination: T): UiEvent<T>
    data class ShowToast(
        @param:StringRes val messageRes: Int = 0,
        val message: String? = null
    ): UiEvent<Nothing> {
        constructor(message: String): this(0, message)

        fun asString(context: Context): String {
            return message ?:context.getString(messageRes)
        }
    }
}