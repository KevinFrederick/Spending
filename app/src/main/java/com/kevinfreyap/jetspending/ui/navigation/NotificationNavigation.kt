package com.kevinfreyap.jetspending.ui.navigation

sealed interface NotificationNavigation {
    data object NavigateUp: NotificationNavigation
}