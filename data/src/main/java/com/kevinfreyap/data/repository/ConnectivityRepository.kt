package com.kevinfreyap.data.repository

import com.kevinfreyap.data.source.NetworkMonitor
import com.kevinfreyap.domain.repository.IConnectivityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class ConnectivityRepository @Inject constructor(
    networkMonitor: NetworkMonitor
): IConnectivityRepository {
    override val isOnline: Flow<Boolean> = networkMonitor.isOnline
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = true
        )
}