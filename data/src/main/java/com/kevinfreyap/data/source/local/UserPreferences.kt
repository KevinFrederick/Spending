package com.kevinfreyap.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.AppTheme
import com.kevinfreyap.domain.model.NotificationPreferences
import com.kevinfreyap.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
){
    fun getUser(): Flow<User?> {
        return dataStore.data.map { preferences ->
            val uid = preferences[USER_UID]

            if (uid != null) {
                val currency = preferences[CURRENCY_KEY] ?: "IDR"

                User(
                    uid = uid,
                    email = preferences[EMAIL_KEY] ?: "",
                    displayName = preferences[USERNAME_KEY] ?: "",
                    photoUrl = preferences[PHOTO_URL_KEY],
                    hasPassword = preferences[HAS_PASSWORD] ?: true,
                    currency = AppCurrency.valueOf(currency)
                )
            } else {
                null
            }
        }
    }

    fun getCurrency(): Flow<AppCurrency> {
        return dataStore.data.map { preferences ->
            val currency = preferences[CURRENCY_KEY] ?: "IDR"
            AppCurrency.valueOf(currency)
        }
    }

    fun getTheme(): Flow<AppTheme> {
        return dataStore.data.map { preferences ->
            val theme = preferences[THEME_KEY] ?: "SYSTEM"
            AppTheme.valueOf(theme)
        }
    }

    fun getNotificationPreferences(): Flow<NotificationPreferences> {
        return dataStore.data.map { preferences ->
            NotificationPreferences(
                isMonthlyEnabled = preferences[MONTHLY_NOTIFICATION] ?: false,
                isDailyEnabled = preferences[DAILY_NOTIFICATION] ?: false,
                reminderHour = preferences[NOTIFICATION_TIME_HOUR] ?: "21",
                reminderMinute = preferences[NOTIFICATION_TIME_MINUTE] ?: "00"
            )
        }
    }

    fun getAppLockPreference(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[APP_LOCK_PREFERENCE] ?: false
        }
    }

    fun getBlockScreenPreference(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[BLOCK_SCREEN_PREFERENCE] ?: true
        }
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            with(user){
                preferences[USER_UID] = uid
                preferences[EMAIL_KEY] = email
                preferences[USERNAME_KEY] = displayName
                preferences[PHOTO_URL_KEY] = photoUrl ?: ""
                preferences[CURRENCY_KEY] = currency.name
                preferences[HAS_PASSWORD] = hasPassword
            }
        }
    }

    suspend fun saveCurrency(appCurrency: AppCurrency) {
        dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = appCurrency.name
        }
    }

    suspend fun saveTheme(appTheme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = appTheme.name
        }
    }

    suspend fun saveNotificationPref(notificationPref: NotificationPreferences) {
        dataStore.edit { preferences ->
            with(notificationPref) {
                preferences[MONTHLY_NOTIFICATION] = isMonthlyEnabled
                preferences[DAILY_NOTIFICATION] = isDailyEnabled
                preferences[NOTIFICATION_TIME_HOUR] = reminderHour
                preferences[NOTIFICATION_TIME_MINUTE] = reminderMinute
            }
        }
    }

    suspend fun saveAppLockPreference(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[APP_LOCK_PREFERENCE] = isEnabled
        }
    }

    suspend fun saveBlockScreenPreference(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BLOCK_SCREEN_PREFERENCE] = isEnabled
        }
    }

    suspend fun updateUser(
        newUsername: String?,
        newImageUrl: String?
    ) {
        dataStore.edit { preferences ->
            if (!newUsername.isNullOrBlank()) preferences[USERNAME_KEY] = newUsername
            if (!newImageUrl.isNullOrBlank()) preferences[PHOTO_URL_KEY] = newImageUrl
        }
    }

    suspend fun updatePasswordStatus(hasPassword: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAS_PASSWORD] = hasPassword
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            val currentTheme = preferences[THEME_KEY]

            preferences.clear()
            if (currentTheme != null) {
                preferences[THEME_KEY] = currentTheme
            }
        }
    }

    companion object {
        private val USER_UID = stringPreferencesKey("uid")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PHOTO_URL_KEY = stringPreferencesKey("photo_url")
        private val HAS_PASSWORD = booleanPreferencesKey("has_password")
        private val THEME_KEY = stringPreferencesKey("theme_mode")
        private val CURRENCY_KEY = stringPreferencesKey("currency")
        private val MONTHLY_NOTIFICATION = booleanPreferencesKey("monthly_notification")
        private val DAILY_NOTIFICATION = booleanPreferencesKey("daily_notification")
        private val NOTIFICATION_TIME_HOUR = stringPreferencesKey("time_notification_hour")
        private val NOTIFICATION_TIME_MINUTE = stringPreferencesKey("time_notification_minute")
        private val APP_LOCK_PREFERENCE = booleanPreferencesKey("app_lock_preference")
        private val BLOCK_SCREEN_PREFERENCE = booleanPreferencesKey("block_screen_preference")
    }
}