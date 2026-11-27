package com.kevinfreyap.domain.usecase.rates

import com.kevinfreyap.domain.repository.IExchangeRatesRepository
import javax.inject.Inject

class EnsureRatesExistUseCase @Inject constructor(
    private val ratesRepository: IExchangeRatesRepository
) {
    suspend operator fun invoke(date: String){
        ratesRepository.ensureRatesExist(date)
    }
}