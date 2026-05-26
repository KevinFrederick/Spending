package com.kevinfreyap.jetspending.ui.main

import com.kevinfreyap.domain.model.AppTheme

sealed interface MainActivityState {
    data object Loading : MainActivityState
    data class Success(
        val theme: AppTheme,
        val startDestination: String,
        val isAppLockEnabled: Boolean,
        val isUnlocked: Boolean
    ) : MainActivityState
}