package com.kevinfreyap.domain.usecase.currency

import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.repository.IUserRepository
import javax.inject.Inject

class SetCurrencyUseCase @Inject constructor(
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(currency: AppCurrency) =
        userRepository.setCurrency(currency)
}