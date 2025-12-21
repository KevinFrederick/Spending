package com.kevinfreyap.domain.usecase.authentication

import com.kevinfreyap.domain.resource.DomainResult
import kotlinx.coroutines.flow.Flow

interface AuthenticationUseCase {
    fun getAuthState(): Flow<Boolean>
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