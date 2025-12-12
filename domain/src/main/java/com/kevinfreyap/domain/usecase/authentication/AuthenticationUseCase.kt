package com.kevinfreyap.domain.usecase.authentication

import com.kevinfreyap.domain.resource.DomainResult

interface AuthenticationUseCase {
    suspend fun isUserLoggedIn(): Boolean
    suspend fun register(
        email: String,
        password: String,
        confirmPassword: String
    ): DomainResult<Unit>
    suspend fun login(
        email: String,
        password: String
    ): DomainResult<Unit>
    suspend fun logout() : DomainResult<Unit>
}