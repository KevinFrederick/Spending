package com.kevinfreyap.domain.usecase.connectivity

import com.kevinfreyap.domain.repository.IConnectivityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConnectivityUseCase @Inject constructor(
    private val connectivityRepository: IConnectivityRepository
) {
    operator fun invoke(): Flow<Boolean> =
        connectivityRepository.isOnline
}