package com.kevinfreyap.data.repository

import androidx.room.withTransaction
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kevinfreyap.data.mapper.UserMapper
import com.kevinfreyap.data.source.local.UserPreferences
import com.kevinfreyap.data.source.local.db.AppDatabase
import com.kevinfreyap.data.source.remote.firebase.UserFirestore
import com.kevinfreyap.data.utils.DataConstants.USER_COLLECTION
import com.kevinfreyap.domain.error.ErrorReporter
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

class AuthenticationRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userPreferences: UserPreferences,
    private val database: AppDatabase,
    private val userMapper: UserMapper,
    private val errorReporter: ErrorReporter,
) : IAuthenticationRepository {

    override fun getAuthState(): Flow<Boolean> = callbackFlow {
        trySend(firebaseAuth.currentUser != null)

        val authListener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            trySend(user != null)
            if (user != null) {
                errorReporter.setUserId(user.uid)
            }
        }

        firebaseAuth.addAuthStateListener(authListener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(authListener)
        }
    }

    override suspend fun authWithGoogle(idToken: String): DomainResult<Unit> {
        return try {
            val authCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(authCredential).await()
            val user = authResult.user

            if (user != null) {
                errorReporter.setUserId(user.uid)
                val userFirestore = updateOrSaveToFirestore(user)
                handleAuthResult(userFirestore)
            } else {
                DomainResult.Failure(Exception("Firebase returned null user after Google authentication"))
            }
        } catch (e: Exception) {
            errorReporter.recordException(e)
            DomainResult.Failure(e)
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        val user = firebaseAuth.currentUser
        if (user != null) {
            errorReporter.setUserId(user.uid)
        }
        return user != null
    }

    override suspend fun register(authRequest: AuthenticationRequest): DomainResult<Unit> {
        return try {
            val response = firebaseAuth.createUserWithEmailAndPassword(
                authRequest.email,
                authRequest.password
            ).await()

            val user = response.user
                ?: return DomainResult.Failure(Exception("Firebase returned null user"))

            errorReporter.setUserId(user.uid)

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
            DomainResult.ValidationFailed(
                listOf(ValidationError.AuthenticationEmailAlreadyUsed)
            )
        } catch (e: Exception) {
            errorReporter.recordException(e)
            DomainResult.Failure(e)
        }
    }

    override suspend fun login(authRequest: AuthenticationRequest): DomainResult<Unit> {
        return try {
            val response = firebaseAuth.signInWithEmailAndPassword(
                authRequest.email,
                authRequest.password
            ).await()

            val user = response.user
                ?: return DomainResult.Failure(Exception("Firebase returned null user"))

            errorReporter.setUserId(user.uid)

            val userFirestore = updateOrSaveToFirestore(user)
            handleAuthResult(userFirestore)
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            DomainResult.ValidationFailed(
                listOf(ValidationError.AuthenticationWrongPassword)
            )
        } catch (_: FirebaseAuthInvalidUserException) {
            DomainResult.ValidationFailed(
                listOf(ValidationError.AuthenticationWrongPassword)
            )
        } catch (e: Exception) {
            errorReporter.recordException(e)
            DomainResult.Failure(e)
        }
    }

    override suspend fun resetPassword(email: String): DomainResult<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            errorReporter.recordException(e)
            DomainResult.Failure(e)
        }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): DomainResult<Unit> {
        if (oldPassword.isBlank()) {
            return DomainResult.ValidationFailed(listOf(ValidationError.AuthenticationPasswordBlank))
        }
        if (newPassword.isBlank()) {
            return DomainResult.ValidationFailed(listOf(ValidationError.AuthenticationNewPasswordBlank))
        }

        return try {
            val currentUser = firebaseAuth.currentUser
                ?: throw Exception("User not logged in")

            val email = currentUser.email
                ?: currentUser.providerData.firstNotNullOfOrNull { it.email }
                ?: throw Exception("User email is missing")

            val credential = EmailAuthProvider.getCredential(email, oldPassword)
            currentUser.reauthenticate(credential).await()
            currentUser.updatePassword(newPassword).await()

            DomainResult.Success(Unit)
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            DomainResult.ValidationFailed(
                listOf(ValidationError.AuthenticationWrongPassword)
            )
        } catch (e: Exception) {
            errorReporter.recordException(e)
            DomainResult.Failure(e)
        }
    }

    override suspend fun createPassword(newPassword: String): DomainResult<Unit> {
        val currentUser = firebaseAuth.currentUser
            ?: return DomainResult.Failure(Exception("User not logged in"))

        return try {
            val email = currentUser.email
                ?: currentUser.providerData.firstNotNullOfOrNull { it.email }
                ?: throw Exception("Email missing")

            val credential = EmailAuthProvider.getCredential(email, newPassword)
            currentUser.linkWithCredential(credential).await()

            currentUser.reload().await()
            updatePasswordStatus(true)

            DomainResult.Success(Unit)
        } catch (_: FirebaseAuthRecentLoginRequiredException) {
            DomainResult.ValidationFailed( listOf(
                ValidationError.AuthenticationReLogin
            ))
        } catch (_: FirebaseAuthUserCollisionException) {
            try {
                currentUser.updatePassword(newPassword).await()
                currentUser.reload().await()
                updatePasswordStatus(true)
                DomainResult.Success(Unit)
            } catch (e: Exception) {
                errorReporter.recordException(e)
                DomainResult.Failure(e)
            }
        } catch (e: Exception) {
            errorReporter.recordException(e)
            DomainResult.Failure(e)
        }
    }

    override suspend fun reauthenticateWithGoogle(idToken: String): DomainResult<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: throw Exception("User not logged in")

            val authCredential = GoogleAuthProvider.getCredential(idToken, null)
            user.reauthenticate(authCredential).await()

            DomainResult.Success(Unit)
        } catch (e: FirebaseAuthException) {
            if (e.errorCode == "ERROR_USER_MISMATCH") {
                DomainResult.ValidationFailed(listOf(ValidationError.AuthenticationUserMismatch))
            } else {
                errorReporter.recordException(e)
                DomainResult.Failure(e)
            }
        } catch (e: Exception) {
            errorReporter.recordException(e)
            DomainResult.Failure(e)
        }
    }

    override suspend fun logout(): DomainResult<Unit> {
        return try {
            userPreferences.clearSession()

            try {
                database.withTransaction {
                    database.transactionDao().deleteAllTransactions()
                }
            } catch (e: Exception) {
                errorReporter.log("Failed to clear database on logout: ${e.message}")
                errorReporter.recordException(e)
            }

            try {
                firebaseAuth.signOut()
            } catch (e: Exception) {
                errorReporter.log("Server logout failed: ${e.message}")
                errorReporter.recordException(e)
            }

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            errorReporter.recordException(e)
            DomainResult.Failure(e)
        }
    }

    override suspend fun deleteAccount(password: String?): DomainResult<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: throw Exception("User not logged in")

            val uid = user.uid

            if (password != null) {
                val email = user.email
                    ?: user.providerData.firstNotNullOfOrNull { it.email }
                    ?: throw Exception("Email missing")
                val credential = EmailAuthProvider.getCredential(email, password)
                user.reauthenticate(credential).await()
            }

            firestore.collection(USER_COLLECTION)
                .document(uid)
                .delete()
                .await()

            userPreferences.clearSession()
            user.delete().await()

            DomainResult.Success(Unit)
        } catch (_: FirebaseAuthRecentLoginRequiredException) {
            DomainResult.ValidationFailed(
                listOf(ValidationError.AuthenticationReLogin)
            )
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            DomainResult.ValidationFailed(
                listOf(ValidationError.AuthenticationWrongPassword)
            )
        } catch (e: Exception) {
            errorReporter.recordException(e)
            DomainResult.Failure(e)
        }
    }

    private suspend fun handleAuthResult(user: UserFirestore): DomainResult<Unit> {
        return try {
            val domainUser = userMapper.mapFirebaseUserToUser(user)
            userPreferences.saveUser(domainUser)
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            errorReporter.recordException(e)
            DomainResult.Failure(e)
        }
    }

    private suspend fun updateOrSaveToFirestore(user: FirebaseUser): UserFirestore {
        val userDocRef = firestore.collection(USER_COLLECTION).document(user.uid)
        val snapshot = userDocRef.get().await()

        return if (snapshot.exists()) {
            try {
                userDocRef.update("lastLogin", FieldValue.serverTimestamp()).await()
            } catch (e: Exception) {
                errorReporter.log("Failed to update lastLogin timestamp: ${e.message}")
            }

            val firestoreUser = snapshot.toObject(UserFirestore::class.java)
                ?: userMapper.mapFirebaseUserToFirestoreUser(user)

            val fallbackName = user.displayName
                ?: user.email?.substringBefore("@")
                ?: "User"

            firestoreUser.copy(
                uid = firestoreUser.uid.ifBlank { user.uid },
                email = firestoreUser.email.ifBlank { user.email ?: "" },
                name = firestoreUser.name.ifBlank { fallbackName }
            )
        } else {
            val fallbackName = user.displayName
                ?: user.email?.substringBefore("@")
                ?: "User"

            try {
                val profileUpdates = userProfileChangeRequest {
                    displayName = fallbackName
                }
                user.updateProfile(profileUpdates).await()
            } catch (e: Exception) {
                errorReporter.log("Failed to update Firebase Auth profile: ${e.message}")
            }

            val restoredUser = userMapper.mapFirebaseUserToFirestoreUser(user).copy(
                name = fallbackName
            )

            try {
                userDocRef.set(restoredUser, SetOptions.merge()).await()
            } catch (e: Exception) {
                errorReporter.log("Failed to save user to Firestore: ${e.message}")
                errorReporter.recordException(e)
            }

            restoredUser
        }
    }

    private suspend fun updatePasswordStatus(hasPassword: Boolean) {
        userPreferences.updatePasswordStatus(hasPassword)

        val currentUserId = firebaseAuth.currentUser?.uid ?: return

        try {
            firestore.collection(USER_COLLECTION)
                .document(currentUserId)
                .set(mapOf("hasPassword" to hasPassword), SetOptions.merge())
                .await()
        } catch (e: Exception) {
            errorReporter.log("Failed to update hasPassword in Firestore: ${e.message}")
            errorReporter.recordException(e)
        }
    }
}