package com.kevinfreyap.data.repository

import android.util.Log
import androidx.room.withTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.kevinfreyap.data.mapper.UserMapper
import com.kevinfreyap.data.source.local.AppDatabase
import com.kevinfreyap.data.source.local.UserPreferences
import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.domain.model.AuthenticationRequest
import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.resource.DomainResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userPreferences: UserPreferences,
    private val database: AppDatabase,
    private val userMapper: UserMapper
): IAuthenticationRepository {
    override fun getAuthState(): Flow<Boolean> = callbackFlow{
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }

        firebaseAuth.addAuthStateListener(authListener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(authListener)
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun register(authRequest: AuthenticationRequest): DomainResult<Unit> {
        return try {
            val response = firebaseAuth.createUserWithEmailAndPassword(
                authRequest.email,
                authRequest.password
            ).await()

            val user = response.user ?: return DomainResult.Failure(Throwable("Firebase returned null user"))

            val defaultName = authRequest.email
                .substringBefore("@")
                .replaceFirstChar { it.uppercase() }

            val profileUpdates = userProfileChangeRequest {
                displayName = defaultName
            }
            user.updateProfile(profileUpdates).await()

            handleAuthResult(user)
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

            val user = response.user ?: return DomainResult.Failure(Throwable("Firebase returned null user"))

            handleAuthResult(user)
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            DomainResult.ValidationFailed(listOf(
                ValidationError.AuthenticationWrongPassword
            ))
        }
        catch (e: Exception) {
            DomainResult.Failure(e)
        }
    }

    override suspend fun logout(): DomainResult<Unit> {
        return try {
            userPreferences.clearSession()
            database.withTransaction {
                database.transactionDao().deleteAllTransactions()
            }

            try {
                firebaseAuth.signOut()
            } catch (e: Exception) {
                Log.e(TAG, "Server logout failed", e)
            }

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(e)
        }
    }

    private suspend fun handleAuthResult(user: FirebaseUser): DomainResult<Unit> {
        return try {
            val domainUser = userMapper.mapFirebaseUserToUser(user)
            userPreferences.saveUser(domainUser)

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(e)
        }
    }

    companion object {
        private const val TAG = "AuthenticationRepository"
    }
}