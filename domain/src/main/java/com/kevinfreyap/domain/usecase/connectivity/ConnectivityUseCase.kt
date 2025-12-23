package com.kevinfreyap.domain.usecase.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityUseCase {
    val isOnline: Flow<Boolean>
}