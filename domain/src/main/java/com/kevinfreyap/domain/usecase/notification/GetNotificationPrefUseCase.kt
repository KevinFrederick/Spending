package com.kevinfreyap.domain.usecase.notification

import com.kevinfreyap.domain.model.NotificationPreferences
import com.kevinfreyap.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationPrefUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    operator fun invoke(): Flow<NotificationPreferences> =
        userRepository.getNotificationPref()
}