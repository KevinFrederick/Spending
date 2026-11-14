package com.kevinfreyap.domain.usecase.user

import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    operator fun invoke(): Flow<AppTheme> =
        userRepository.getCurrentTheme()
}