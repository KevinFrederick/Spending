package com.kevinfreyap.domain.model

enum class AppCurrency (
    val displayName: String,
    val displayCodeName: String,
    val isFraction: Boolean
) {
    IDR (
        displayName = "Indonesian Rupiah (Rp)",
        displayCodeName = "IDR • Indonesian Rupiah",
        isFraction = false
    ),
    USD(
        displayName = "US Dollar (US$)",
        displayCodeName = "USD • US Dollar",
        isFraction = true
    ),
    TWD (
        displayName = "Taiwan Dollar (NT$)",
        displayCodeName = "TWD • Taiwan Dollar",
        isFraction = true
    ),
    SGD(
        displayName = "Singapore Dollar (SG$)",
        displayCodeName = "SGD • Singapore Dollar",
        isFraction = true
    ),
    MYR (
        displayName = "Malaysian Ringgit (RM)",
        displayCodeName = "MYR • Malaysian Ringgit",
        isFraction = true
    ),
    JPY(
        displayName = "Japanese Yen (JP¥)",
        displayCodeName = "JPY • Japanese Yen",
        isFraction = false
    );
    val decimalPlaces: Int
        get() = if (isFraction) 2 else 0

    companion object {
        val DEFAULT = IDR
    }
}