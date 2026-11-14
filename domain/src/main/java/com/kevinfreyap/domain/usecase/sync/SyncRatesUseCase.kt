package com.kevinfreyap.domain.usecase.sync

import com.kevinfreyap.domain.repository.IExchangeRatesRepository
import javax.inject.Inject

class SyncRatesUseCase @Inject constructor(
    private val ratesRepository: IExchangeRatesRepository
) {
    suspend operator fun invoke() = ratesRepository.syncDailyRates()
}