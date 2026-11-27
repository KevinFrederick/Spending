package com.kevinfreyap.domain.usecase.currency

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrencyUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    operator fun invoke(): Flow<AppCurrency> =
        userRepository.getSelectedCurrency()
}