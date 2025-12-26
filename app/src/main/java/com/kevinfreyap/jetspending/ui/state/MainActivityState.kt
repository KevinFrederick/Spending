package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.model.AppTheme

sealed interface MainActivityState {
    data object Loading : MainActivityState
    data class Success(
        val theme: AppTheme,
        val startDestination: String,
    ) : MainActivityState
}
