package com.kevinfreyap.jetspending.ui.model

import androidx.compose.ui.graphics.Color

data class SettingsItem(
    val id: SettingsOption,
    val title: Int,
    val subtitle: String? = null,
    val subtitleRes: Int? = null,
    val icon: Int? = null,
    val showChevron: Boolean = true,
    val contentColor: Color? = null
)
