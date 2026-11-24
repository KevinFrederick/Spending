package com.kevinfreyap.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinfreyap.data.mapper.NotificationPrefMapper
import com.kevinfreyap.data.source.local.UserPreferences
import com.kevinfreyap.data.source.remote.firebase.UserFirestore
import com.kevinfreyap.data.utils.DataConstants.NOTIFICATION_SUB_DOCUMENT
import com.kevinfreyap.data.utils.DataConstants.SETTINGS_SUB_COLLECTION
import com.kevinfreyap.data.utils.DataConstants.USER_COLLECTION
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.domain.model.NotificationPreferences
import com.kevinfreyap.domain.model.User
import com.kevinfreyap.domain.repository.IUserRepository
import com.kevinfreyap.domain.resource.DomainResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userPreferences: UserPreferences,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val notificationMapper: NotificationPrefMapper
): IUserRepository {
    override fun getUserProfile(): Flow<User?> = callbackFlow {
        val preferencesJob = launch {
            userPreferences.getUser().collect { localUser ->
                val currentUser = firebaseAuth.currentUser

                val isUserMismatchOrMissing = localUser == null ||
                        (currentUser != null && localUser.uid != currentUser.uid) ||
                        localUser.uid.isBlank() ||
                        localUser.email.isBlank() ||
                        localUser.displayName.isBlank()

                if (!isUserMismatchOrMissing) {
                    trySend(localUser)
                } else if (currentUser != null) {
                    val isSameUser = localUser?.uid == currentUser.uid

                    val fallbackName = currentUser.displayName
                        ?.ifBlank { null }
                        ?: currentUser.email?.substringBefore("@")
                        ?.replaceFirstChar { it.uppercase() }
                        ?: "User"

                    val fallbackUser = User(
                        uid = currentUser.uid,
                        email = if (isSameUser && localUser.email.isNotBlank()) localUser.email else (currentUser.email ?: ""),
                        displayName = if (isSameUser && localUser.displayName.isNotBlank()) localUser.displayName else fallbackName,
                        photoUrl = if (isSameUser && !localUser.photoUrl.isNullOrBlank()) localUser.photoUrl else currentUser.photoUrl?.toString(),
                        hasPassword = localUser.takeIf { isSameUser }?.hasPassword ?: true,
                        currency = localUser.takeIf { isSameUser }?.currency ?: AppCurrency.DEFAULT
                    )

                    trySend(fallbackUser)

                    this@callbackFlow.launch(Dispatchers.IO) {
                        try {
                            val snapshot = firestore.collection(USER_COLLECTION)
                                .document(currentUser.uid)
                                .get()
                                .await()

                            if (snapshot.exists()) {
                                val firestoreUser = snapshot.toObject(UserFirestore::class.java)
                                if (firestoreUser != null) {
                                    val updatedUser = User(
                                        uid = currentUser.uid,
                                        email = firestoreUser.email.ifBlank { currentUser.email ?: "" },
                                        displayName = firestoreUser.name.ifBlank { fallbackName },
                                        photoUrl = firestoreUser.photoUrl.ifBlank { currentUser.photoUrl?.toString() },
                                        hasPassword = firestoreUser.hasPassword,
                                        currency = try {
                                            AppCurrency.valueOf(firestoreUser.currency)
                                        } catch (_: Exception) {
                                            fallbackUser.currency
                                        }
                                    )
                                    userPreferences.saveUser(updatedUser)
                                } else {
                                    userPreferences.saveUser(fallbackUser)
                                }
                            } else {
                                userPreferences.saveUser(fallbackUser)
                            }
                        } catch (_: Exception) {
                            userPreferences.saveUser(fallbackUser)
                        }
                    }
                } else {
                    trySend(null)
                }
            }
        }

        awaitClose {
            preferencesJob.cancel()
        }
    }

    override fun getSelectedCurrency(): Flow<AppCurrency> = userPreferences.getCurrency()

    override fun getCurrentTheme(): Flow<AppTheme> = userPreferences.getTheme()

    override fun getNotificationPref(): Flow<NotificationPreferences> = userPreferences.getNotificationPreferences()

    override suspend fun updateUserProfile(
        userId: String,
        newUsername: String?,
    ): DomainResult<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>()

            if (!newUsername.isNullOrBlank()) updates["name"] = newUsername

            if (updates.isNotEmpty()) {
                firestore.collection(USER_COLLECTION)
                    .document(userId)
                    .update(updates)
                    .await()

                userPreferences.updateUser(
                    newUsername = newUsername,
                )
            }

            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Failure(e)
        }
    }

    override suspend fun setCurrency(appCurrency: AppCurrency) {
        userPreferences.saveCurrency(appCurrency)

        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            try {
                firestore.collection(USER_COLLECTION)
                    .document(currentUser.uid)
                    .update("currency", appCurrency.name)
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun setNotificationPref(notificationPref: NotificationPreferences) {
        userPreferences.saveNotificationPref(notificationPref)

        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            try {
                val notificationFirestore = notificationMapper.mapNotificationPreferenceToFirestore(notificationPref)

                firestore.collection(USER_COLLECTION)
                    .document(currentUser.uid)
                    .collection(SETTINGS_SUB_COLLECTION)
                    .document(NOTIFICATION_SUB_DOCUMENT)
                    .set(notificationFirestore)
                    .await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun setTheme(appTheme: AppTheme) {
        userPreferences.saveTheme(appTheme)
    }
}