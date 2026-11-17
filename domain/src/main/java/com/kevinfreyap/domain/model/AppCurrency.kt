package com.kevinfreyap.domain.model

enum class AppCurrency (
    val symbol: String,
    val countryName: String,
    val displayName: String,
    val displayLongName: String,
    val displayCurrencyName: String,
    val displayCodeName: String,
    val isFraction: Boolean
) {
    IDR (
        symbol = "Rp",
        countryName = "Indonesia",
        displayName = "Indonesia (Rp)",
        displayLongName = "Indonesia (Rupiah | Rp)",
        displayCurrencyName = "Rupiah | Rp",
        displayCodeName = "IDR • Indonesian Rupiah",
        isFraction = false
    ),
    JPY(
        symbol = "¥",
        countryName = "Japan",
        displayName = "Japan (¥)",
        displayLongName = "Japan (Japanese Yen | ¥)",
        displayCurrencyName = "Japanese Yen | ¥",
        displayCodeName = "JPY • Japanese Yen",
        isFraction = false
    ),
    MYR (
        symbol = "RM",
        countryName = "Malaysia",
        displayName = "Malaysia (RM)",
        displayLongName = "Malaysia (Ringgit Malaysia | RM)",
        displayCurrencyName = "Ringgit Malaysia | RM",
        displayCodeName = "MYR • Malaysian Ringgit",
        isFraction = true
    ),
    SGD(
        symbol = "S$",
        countryName = "Singapore",
        displayName = "Singapore (SG$)",
        displayLongName = "Singapore (Singapore Dollar | SG$)",
        displayCurrencyName = "Singapore Dollar | SG$",
        displayCodeName = "SGD • Singapore Dollar",
        isFraction = true
    ),
    TWD (
        symbol = "NT$",
        countryName = "Taiwan",
        displayName = "Taiwan (NT$)",
        displayLongName = "Taiwan (New Taiwan Dollar | NT$)",
        displayCurrencyName = "New Taiwan Dollar | NT$",
        displayCodeName = "TWD • Taiwan Dollar",
        isFraction = true
    ),
    USD(
        symbol = "US$",
        countryName = "United States",
        displayName = "United States (USD)",
        displayLongName = "United States (US Dollar | US$)",
        displayCurrencyName = "US Dollar | US$",
        displayCodeName = "USD • US Dollar",
        isFraction = true
    );
    val decimalPlaces: Int
        get() = if (isFraction) 2 else 0

    companion object {
        val DEFAULT = IDR
    }
}