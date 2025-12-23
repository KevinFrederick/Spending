package com.kevinfreyap.domain.repository

import kotlinx.coroutines.flow.Flow

interface IConnectivityRepository {
    val isOnline: Flow<Boolean>
}