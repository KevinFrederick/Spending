package com.kevinfreyap.domain.repository

import com.kevinfreyap.domain.model.AuthenticationRequest
import com.kevinfreyap.domain.model.User
import com.kevinfreyap.domain.resource.DomainResult

interface IAuthenticationRepository {
    suspend fun isUserLoggedIn(): Boolean
    suspend fun register(authRequest: AuthenticationRequest): DomainResult<Unit>
    suspend fun login(authRequest: AuthenticationRequest): DomainResult<Unit>
    suspend fun logout(): DomainResult<Unit>
}