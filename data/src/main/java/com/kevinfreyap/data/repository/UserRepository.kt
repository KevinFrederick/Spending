package com.kevinfreyap.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kevinfreyap.data.source.local.UserPreferences
import com.kevinfreyap.data.utils.DataConstants.USER_COLLECTION
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.domain.model.User
import com.kevinfreyap.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userPreferences: UserPreferences,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): IUserRepository {
    override fun getUserProfile(): Flow<User?> = userPreferences.getUser()

    override fun getSelectedCurrency(): Flow<AppCurrency> = userPreferences.getCurrency()

    override fun getCurrentTheme(): Flow<AppTheme> = userPreferences.getTheme()

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

    override suspend fun setTheme(appTheme: AppTheme) {
        userPreferences.saveTheme(appTheme)
    }
}