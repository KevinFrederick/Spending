package com.kevinfreyap.jetspending.ui.main

import androidx.compose.runtime.compositionLocalOf
import com.kevinfreyap.domain.model.AppCurrency

val LocalBaseCurrency = compositionLocalOf { AppCurrency.IDR }