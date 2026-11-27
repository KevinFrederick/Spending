package com.kevinfreyap.domain.usecase.user

import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.domain.repository.IUserRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(theme: AppTheme) =
        userRepository.setTheme(theme)
}