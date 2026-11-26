package com.kevinfreyap.jetspending.ui.state

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class DynamicString(val value: String): UiText
    class StringResource(@StringRes val resId: Int, vararg val args: Any): UiText

    @Composable
    fun asString(): String = when(this) {
        is DynamicString -> value
        is StringResource -> stringResource(
            resId,
            *args.map { if (it is UiText) it.asString() else it }
                .toTypedArray()
        )
    }
}