package com.kevinfreyap.jetspending.ui.screen.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevinfreyap.domain.model.NotificationPreferences
import com.kevinfreyap.domain.usecase.notification.NotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationUseCase: NotificationUseCase
): ViewModel() {
    val notificationState: StateFlow<NotificationPreferences?> = notificationUseCase.getNotificationPref()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun onMonthlySwitch(isChecked: Boolean) {
        viewModelScope.launch {
            notificationState.value?.let { currentState ->
                notificationUseCase.setNotificationPref(
                    currentState.copy(
                        isMonthlyEnabled = isChecked
                    )
                )
            }
        }
    }

    fun onDailySwitch(isChecked: Boolean) {
        viewModelScope.launch {
            notificationState.value?.let { currentState ->
                notificationUseCase.setNotificationPref(
                    currentState.copy(
                        isDailyEnabled = isChecked
                    )
                )
            }
        }
    }

    fun onUpdateTime(time: String) {
        val timeParts = time.split(':')

        val hour = timeParts.getOrNull(0) ?: "21"
        val minute = timeParts.getOrNull(1) ?: "00"

        viewModelScope.launch {
            notificationState.value?.let { currentState ->
                notificationUseCase.setNotificationPref(
                    currentState.copy(
                        reminderHour = hour,
                        reminderMinute = minute
                    )
                )
            }
        }
    }
}