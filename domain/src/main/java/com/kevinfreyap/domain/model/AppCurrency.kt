package com.kevinfreyap.domain.model

enum class AppCurrency (
    val symbol: String,
    val countryName: String,
    val displayName: String,
    val displayLongName: String,
    val displayCurrencyName: String,
    val isFraction: Boolean
) {
    IDR (
        symbol = "Rp",
        countryName = "Indonesia",
        displayName = "Indonesia (Rp)",
        displayLongName = "Indonesia (Rupiah | Rp)",
        displayCurrencyName = "Rupiah | Rp",
        isFraction = false
    ),
    JPY(
        symbol = "¥",
        countryName = "Japan",
        displayName = "Japan (¥)",
        displayLongName = "Japan (Japanese Yen | ¥)",
        displayCurrencyName = "Japanese Yen | ¥",
        isFraction = false
    ),
    MYR (
        symbol = "RM",
        countryName = "Malaysia",
        displayName = "Malaysia (RM)",
        displayLongName = "Malaysia (Ringgit Malaysia | RM)",
        displayCurrencyName = "Ringgit Malaysia | RM",
        isFraction = true
    ),
    SGD(
        symbol = "SG$",
        countryName = "Singapore",
        displayName = "Singapore (SG$)",
        displayLongName = "Singapore (Singapore Dollar | SG$)",
        displayCurrencyName = "Singapore Dollar | SG$",
        isFraction = true
    ),
    TWD (
        symbol = "NT$",
        countryName = "Taiwan",
        displayName = "Taiwan (NT$)",
        displayLongName = "Taiwan (New Taiwan Dollar | NT$)",
        displayCurrencyName = "New Taiwan Dollar | NT$",
        isFraction = true
    ),
    USD(
        symbol = "US$",
        countryName = "United States",
        displayName = "United States (USD)",
        displayLongName = "United States (US Dollar | US$)",
        displayCurrencyName = "US Dollar | US$",
        isFraction = true
    ),
}