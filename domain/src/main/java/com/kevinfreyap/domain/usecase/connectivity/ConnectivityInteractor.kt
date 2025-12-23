package com.kevinfreyap.domain.usecase.connectivity

import com.kevinfreyap.domain.repository.IConnectivityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConnectivityInteractor @Inject constructor(
    connectivityRepository: IConnectivityRepository,
): ConnectivityUseCase {
    override val isOnline: Flow<Boolean> = connectivityRepository.isOnline
}