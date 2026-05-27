package com.kevinfreyap.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.room.withTransaction
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinfreyap.data.BuildConfig
import com.kevinfreyap.data.mapper.UserMapper
import com.kevinfreyap.data.source.local.AppDatabase
import com.kevinfreyap.data.source.local.UserPreferences
import com.kevinfreyap.data.source.remote.firebase.UserFirestore
import com.kevinfreyap.data.utils.DataConstants.USER_COLLECTION
import com.kevinfreyap.domain.error.ValidationError
import com.kevinfreyap.domain.model.AuthenticationRequest
import com.kevinfreyap.domain.repository.IAuthenticationRepository
import com.kevinfreyap.domain.resource.DomainResult
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val firestore: FirebaseFirestore,
    private val userPreferences: UserPreferences,
    private val database: AppDatabase,
    private val userMapper: UserMapper,
    @param:ApplicationContext private val context: Context
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

    override suspend fun authWithGoogle(activity: Activity): DomainResult<Unit> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(activity, request)

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleCredentials = GoogleIdTokenCredential.createFrom(credential.data)

                val authCredential = GoogleAuthProvider.getCredential(googleCredentials.idToken, null)
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()
                val user = authResult.user

                if (user != null) {
                    val userFirestore = updateOrSaveToFirestore(user)
                    handleAuthResult(userFirestore)
                } else {
                    DomainResult.Failure(Exception("User is Null"))
                }
            } else {
                DomainResult.Failure(Exception("Invalid Credential Type"))
            }
        }catch (_: GetCredentialCancellationException) {
            DomainResult.Failure(Exception("Sign In Canceled"))
        } catch (e: Exception) {
            DomainResult.Failure(e)
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

            val userFirestore = userMapper.mapFirebaseUserToFirestoreUser(user).copy(
                name = defaultName
            )

            firestore.collection(USER_COLLECTION)
                .document(user.uid)
                .set(userFirestore)
                .await()

            handleAuthResult(userFirestore)
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

            val userFirestore = updateOrSaveToFirestore(user)

            handleAuthResult(userFirestore)
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            DomainResult.ValidationFailed(listOf(
                ValidationError.AuthenticationWrongPassword
            ))
        }
        catch (e: Exception) {
            DomainResult.Failure(e)
        }
    }

    override suspend fun resetPassword(email: String): DomainResult<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(e)
        }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): DomainResult<Unit> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: throw Exception("User not logged in")
            val email = currentUser.email
                ?: currentUser.providerData.firstNotNullOfOrNull { it.email }
                ?: throw Exception("User email is missing")

            val credential = EmailAuthProvider.getCredential(email, oldPassword)

            currentUser.reauthenticate(credential).await()

            currentUser.updatePassword(newPassword).await()

            DomainResult.Success(Unit)
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            DomainResult.ValidationFailed( listOf(
                ValidationError.AuthenticationWrongPassword
            ))

        } catch (e: Exception) {
            DomainResult.Failure(e)
        }
    }

    override suspend fun createPassword(newPassword: String): DomainResult<Unit> {
        return try {
            val currentUser = firebaseAuth.currentUser ?: throw Exception("User not logged in")

            currentUser.updatePassword(newPassword).await()

            currentUser.reload().await()

            updatePasswordStatus(true)

            DomainResult.Success(Unit)
        } catch (_: FirebaseAuthRecentLoginRequiredException) {
            DomainResult.ValidationFailed( listOf(
                ValidationError.AuthenticationReLogin
            ))
        } catch (e: Exception) {
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

    private suspend fun handleAuthResult(user: UserFirestore): DomainResult<Unit> {
        return try {
            val domainUser = userMapper.mapFirebaseUserToUser(user)
            userPreferences.saveUser(domainUser)

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(e)
        }
    }

    private suspend fun updateOrSaveToFirestore(user: FirebaseUser): UserFirestore {
        val userDocRef = firestore.collection(USER_COLLECTION).document(user.uid)
        val snapshot = userDocRef.get().await()

        return if (snapshot.exists()) {
            userDocRef.update("lastLogin", FieldValue.serverTimestamp()).await()

            snapshot.toObject(UserFirestore::class.java) ?: throw IllegalStateException("Failed to map document ${user.uid} to FirestoreUser")
        } else {
            val fallbackName = user.displayName ?: user.email?.substringBefore("@") ?: "User"

            // Save to FireAuth Too
            val profileUpdates = userProfileChangeRequest {
                displayName = fallbackName
            }
            user.updateProfile(profileUpdates).await()

            val restoredUser = userMapper.mapFirebaseUserToFirestoreUser(user).copy(
                name = fallbackName
            )
            userDocRef.set(restoredUser).await()
            restoredUser
        }
    }

    private suspend fun updatePasswordStatus(hasPassword: Boolean) {
        userPreferences.updatePasswordStatus(hasPassword)

        val currentUserId = firebaseAuth.currentUser?.uid ?: return

        firestore.collection(USER_COLLECTION)
            .document(currentUserId)
            .update("hasPassword", hasPassword)
            .await()
    }

    companion object {
        private const val TAG = "AuthenticationRepository"
    }
}