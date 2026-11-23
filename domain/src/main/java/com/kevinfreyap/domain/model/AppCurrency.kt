package com.kevinfreyap.domain.model

enum class AppCurrency (
    val symbol: String,
    val displayName: String,
    val displayCodeName: String,
    val isFraction: Boolean
) {
    IDR (
        symbol = "Rp",
        displayName = "Indonesian Rupiah (Rp)",
        displayCodeName = "IDR • Indonesian Rupiah",
        isFraction = false
    ),
    JPY(
        symbol = "JP¥",
        displayName = "Japanese Yen (JP¥)",
        displayCodeName = "JPY • Japanese Yen",
        isFraction = false
    ),
    MYR (
        symbol = "RM",
        displayName = "Malaysian Ringgit (RM)",
        displayCodeName = "MYR • Malaysian Ringgit",
        isFraction = true
    ),
    SGD(
        symbol = "S$",
        displayName = "Singapore Dollar (SG$)",
        displayCodeName = "SGD • Singapore Dollar",
        isFraction = true
    ),
    TWD (
        symbol = "NT$",
        displayName = "Taiwan Dollar (NT$)",
        displayCodeName = "TWD • Taiwan Dollar",
        isFraction = true
    ),
    USD(
        symbol = "US$",
        displayName = "US Dollar (US$)",
        displayCodeName = "USD • US Dollar",
        isFraction = true
    );
    val decimalPlaces: Int
        get() = if (isFraction) 2 else 0

    companion object {
        val DEFAULT = IDR
    }
}