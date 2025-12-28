package com.kevinfreyap.domain.repository

import android.app.Activity
import com.kevinfreyap.domain.model.AuthenticationRequest
import com.kevinfreyap.domain.resource.DomainResult
import kotlinx.coroutines.flow.Flow

interface IAuthenticationRepository {
    fun getAuthState(): Flow<Boolean>
    suspend fun authWithGoogle(activity: Activity): DomainResult<Unit>
    suspend fun isUserLoggedIn(): Boolean
    suspend fun register(authRequest: AuthenticationRequest): DomainResult<Unit>
    suspend fun login(authRequest: AuthenticationRequest): DomainResult<Unit>
    suspend fun resetPassword(email: String): DomainResult<Unit>
    suspend fun logout(): DomainResult<Unit>
}