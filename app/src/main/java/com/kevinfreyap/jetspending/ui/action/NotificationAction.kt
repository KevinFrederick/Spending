package com.kevinfreyap.jetspending.ui.action

sealed interface NotificationAction {
    data class OnShowTimeDialog(val isVisible: Boolean): NotificationAction
    data class OnConfirmTime(val hour: Int, val minute: Int): NotificationAction
    data object OnToggleMasterNotification: NotificationAction
    data object OnToggleWeeklyNotification: NotificationAction
    data object OnToggleDailyNotification: NotificationAction
}