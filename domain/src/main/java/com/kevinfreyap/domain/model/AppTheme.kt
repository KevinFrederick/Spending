package com.kevinfreyap.domain.model

enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM;
    companion object {
        val DEFAULT = SYSTEM
    }
}