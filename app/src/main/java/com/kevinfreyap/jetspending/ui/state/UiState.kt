package com.kevinfreyap.jetspending.ui.state

import com.kevinfreyap.domain.error.Field

sealed class UiState<out T>{
    object Idle: UiState<Nothing>()
    object Loading: UiState<Nothing>()
    data class Success<T>(val data: T): UiState<T>()
    data class ValidationErrors(val errors: Map<Field, Int>): UiState<Nothing>()
    data class Failure(val throwable: Throwable): UiState<Nothing>()
}