package com.kevinfreyap.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kevinfreyap.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
){
    fun getUser(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                uid = preferences[USER_UID],
                email = preferences[EMAIL_KEY],
                displayName = preferences[USERNAME_KEY],
                photoUrl = preferences[PHOTO_URL_KEY],
                isGoogleAccount = preferences[IS_GOOGLE_KEY]
            )
        }
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            with(user){
                preferences[USER_UID] = uid ?: ""
                preferences[EMAIL_KEY] = email ?: ""
                preferences[USERNAME_KEY] = displayName ?: ""
                preferences[PHOTO_URL_KEY] = photoUrl ?: ""
                preferences[IS_GOOGLE_KEY] = isGoogleAccount ?: false
            }
        }
    }

    companion object {
        private val USER_UID = stringPreferencesKey("uid")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PHOTO_URL_KEY = stringPreferencesKey("photo_url")
        private val IS_GOOGLE_KEY = booleanPreferencesKey("is_google")
    }
}