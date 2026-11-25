package com.kevinfreyap.jetspending.ui.screen.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.NotificationPreferences
import com.kevinfreyap.domain.usecase.notification.GetNotificationPrefUseCase
import com.kevinfreyap.domain.usecase.notification.SetNotificationPrefUseCase
import com.kevinfreyap.jetspending.ui.action.NotificationAction
import com.kevinfreyap.jetspending.ui.state.NotificationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    getNotificationPref: GetNotificationPrefUseCase,
    private val setNotificationPref: SetNotificationPrefUseCase
): ViewModel() {
    private val _isReminderDialogVisible = MutableStateFlow(false)

    val state: StateFlow<NotificationState> = combine(
        getNotificationPref(),
        _isReminderDialogVisible
    ) { prefs, isDialogVisible ->
        NotificationState(
            isMasterNotificationEnabled = prefs.isMasterEnabled,
            isWeeklyNotificationEnabled = prefs.isWeeklyEnabled,
            isDailyNotificationEnabled = prefs.isDailyEnabled,
            isReminderDialogVisible = isDialogVisible,
            reminderHour = prefs.reminderHour,
            reminderMinute = prefs.reminderMinute
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotificationState()
    )

    fun onAction(action: NotificationAction) {
        when(action) {
            is NotificationAction.OnConfirmTime -> confirmTime(action.hour, action.minute)
            is NotificationAction.OnShowTimeDialog -> {
                _isReminderDialogVisible.value = action.isVisible
            }
            NotificationAction.OnToggleMasterNotification -> toggleMasterNotification()
            NotificationAction.OnToggleWeeklyNotification -> toggleWeeklyNotification()
            NotificationAction.OnToggleDailyNotification -> toggleDailyNotification()
        }
    }

    private fun confirmTime(hour: Int, minute: Int) {
        _isReminderDialogVisible.value = false
        viewModelScope.launch {
            val currentState = state.value
            setNotificationPref(
                NotificationPreferences(
                    isMasterEnabled = currentState.isMasterNotificationEnabled,
                    isWeeklyEnabled = currentState.isWeeklyNotificationEnabled,
                    isDailyEnabled = currentState.isDailyNotificationEnabled,
                    reminderHour = hour,
                    reminderMinute = minute
                )
            )
        }
    }

    private fun toggleMasterNotification() {
        viewModelScope.launch {
            val currentState = state.value
            val newMasterValue = !currentState.isMasterNotificationEnabled

            setNotificationPref(
                NotificationPreferences(
                    isMasterEnabled = newMasterValue,
                    isWeeklyEnabled = newMasterValue,
                    isDailyEnabled = newMasterValue,
                    reminderHour = currentState.reminderHour,
                    reminderMinute = currentState.reminderMinute
                )
            )
        }
    }

    private fun toggleWeeklyNotification() {
        viewModelScope.launch {
            val currentState = state.value
            if (!currentState.isMasterNotificationEnabled) return@launch

            val newWeekly = !currentState.isWeeklyNotificationEnabled
            setNotificationPref(
                NotificationPreferences(
                    isMasterEnabled = true,
                    isWeeklyEnabled = newWeekly,
                    isDailyEnabled = currentState.isDailyNotificationEnabled,
                    reminderHour = currentState.reminderHour,
                    reminderMinute = currentState.reminderMinute
                )
            )
        }
    }

    private fun toggleDailyNotification() {
        viewModelScope.launch {
            val currentState = state.value
            if (!currentState.isMasterNotificationEnabled) return@launch

            val newDaily = !currentState.isDailyNotificationEnabled
            setNotificationPref(
                NotificationPreferences(
                    isMasterEnabled = true,
                    isWeeklyEnabled = currentState.isWeeklyNotificationEnabled,
                    isDailyEnabled = newDaily,
                    reminderHour = currentState.reminderHour,
                    reminderMinute = currentState.reminderMinute
                )
            )
        }
    }
}
