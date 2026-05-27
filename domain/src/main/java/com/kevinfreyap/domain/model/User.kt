package com.kevinfreyap.domain.model

data class User(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
    val hasPassword: Boolean,
    val currency: AppCurrency
)
