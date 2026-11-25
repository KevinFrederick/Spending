package com.kevinfreyap.jetspending.ui.screen.settings.state

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class SettingsItemState(
    val type: SettingsItemType,
    @DrawableRes val iconRes: Int,
    @StringRes val name: Int,
    @StringRes val description: Int,
    val selectedItem: String? = null,
    val isChecked: Boolean? = null,
    val enabled: Boolean = true
)
