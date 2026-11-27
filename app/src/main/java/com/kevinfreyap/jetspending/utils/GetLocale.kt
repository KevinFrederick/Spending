package com.kevinfreyap.jetspending.utils

import com.kevinfreyap.domain.model.AppCurrency
import java.util.Locale
import java.util.Locale.Builder
import java.util.Locale.US

fun AppCurrency.getLocale(): Locale {
    return when(this) {
        AppCurrency.USD -> {
            US
        }
        AppCurrency.IDR -> {
            Builder()
                .setLanguage("id")
                .setRegion("ID")
                .build()
        }
        AppCurrency.TWD -> {
            Builder()
                .setLanguage("zh")
                .setRegion("TW")
                .build()
        }
        AppCurrency.MYR -> {
            Builder()
                .setLanguage("ms")
                .setRegion("MY")
                .build()
        }
        AppCurrency.SGD -> {
            Builder()
                .setLanguage("en")
                .setRegion("SG")
                .build()
        }
        AppCurrency.JPY -> {
            Builder()
                .setLanguage("ja")
                .setRegion("JP")
                .build()
        }
    }
}