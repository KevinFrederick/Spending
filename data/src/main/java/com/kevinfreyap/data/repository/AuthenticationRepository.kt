package com.kevinfreyap.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.kevinfreyap.data.mapper.UserMapper
import com.kevinfreyap.data.source.local.UserPreferences
import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.domain.model.AuthenticationRequest
import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.resource.DomainResult
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userPreferences: UserPreferences,
    private val userMapper: UserMapper
): IAuthenticationRepository {
    override suspend fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun register(authRequest: AuthenticationRequest): DomainResult<Unit> {
        return try {
            val response = firebaseAuth.createUserWithEmailAndPassword(
                authRequest.email,
                authRequest.password
            ).await()

            handleAuthResult(response)
        } catch (_: FirebaseAuthUserCollisionException) {
            DomainResult.ValidationFailed(listOf(
                ValidationError.AuthenticationEmailAlreadyUsed
            ))
        }
        catch (e: Exception){
            DomainResult.Failure(e)
        }
    }

    override suspend fun login(authRequest: AuthenticationRequest): DomainResult<Unit> {
        return try {
            val response = firebaseAuth.signInWithEmailAndPassword(
                authRequest.email,
                authRequest.password
            ).await()

            handleAuthResult(response)
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            DomainResult.ValidationFailed(listOf(
                ValidationError.AuthenticationWrongPassword
            ))
        }
        catch (e: Exception) {
            DomainResult.Failure(e)
        }
    }

    private suspend fun handleAuthResult(response: AuthResult): DomainResult<Unit> {
        val user = response.user ?: return DomainResult.Failure(Throwable("Firebase returned null user"))
        return try {
            val domainUser = userMapper.mapFirebaseUserToUser(user)
            userPreferences.saveUser(domainUser)

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(e)
        }
    }
}