package com.kevinfreyap.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinfreyap.data.mapper.NotificationPrefMapper
import com.kevinfreyap.data.source.local.UserPreferences
import com.kevinfreyap.data.utils.DataConstants.NOTIFICATION_SUB_DOCUMENT
import com.kevinfreyap.data.utils.DataConstants.SETTINGS_SUB_COLLECTION
import com.kevinfreyap.data.utils.DataConstants.USER_COLLECTION
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.domain.model.NotificationPreferences
import com.kevinfreyap.domain.model.User
import com.kevinfreyap.domain.repository.IUserRepository
import com.kevinfreyap.domain.resource.DomainResult
import kotlinx.coroutines.flow.Flow
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
    override fun getUserProfile(): Flow<User?> = userPreferences.getUser()

    override fun getSelectedCurrency(): Flow<AppCurrency> = userPreferences.getCurrency()

    override fun getCurrentTheme(): Flow<AppTheme> = userPreferences.getTheme()

    override fun getNotificationPref(): Flow<NotificationPreferences> = userPreferences.getNotificationPreferences()

    override suspend fun updateUserProfile(
        userId: String,
        newUsername: String?,
        newImageUrl: String?
    ): DomainResult<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>()

            if (!newUsername.isNullOrBlank()) updates["name"] = newUsername
            if (!newImageUrl.isNullOrBlank()) updates["photoUrl"] = newImageUrl

            if (updates.isNotEmpty()) {
                firestore.collection(USER_COLLECTION)
                    .document(userId)
                    .update(updates)
                    .await()

                userPreferences.updateUser(
                    newUsername = newUsername,
                    newImageUrl = newImageUrl
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