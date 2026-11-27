package com.kevinfreyap.domain.usecase.currency

import com.kevinfreyap.domain.model.ExchangeRates
import com.kevinfreyap.domain.repository.IExchangeRatesRepository
import com.kevinfreyap.domain.usecase.rates.EnsureRatesExistUseCase
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetExchangeRatesByDateUseCase @Inject constructor(
    private val ratesRepository: IExchangeRatesRepository,
    private val ensureRatesExist: EnsureRatesExistUseCase
) {
    suspend operator fun invoke(
        date: Instant,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): ExchangeRates? {
        val selectedDate = date.atZone(zoneId).toLocalDate()
        val today = LocalDate.now(zoneId)

        val effectiveDate = if (selectedDate.isAfter(today)) today else selectedDate
        val dateKey = effectiveDate.format(DateTimeFormatter.ISO_LOCAL_DATE) // e.g. "2025-12-31"

        ensureRatesExist(dateKey)

        return ratesRepository.getRates(dateKey)
    }
}