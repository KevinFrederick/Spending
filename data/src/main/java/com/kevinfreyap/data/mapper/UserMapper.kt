package com.kevinfreyap.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.kevinfreyap.domain.model.User
import javax.inject.Inject

class UserMapper @Inject constructor() {
    fun mapFirebaseUserToUser(firebaseUser: FirebaseUser): User {
        val isGoogleAccount = firebaseUser.providerData.any { userInfo ->
            userInfo.providerId == GoogleAuthProvider.PROVIDER_ID
        }
        return User(
            uid = firebaseUser.uid,
            email = firebaseUser.email,
            displayName = firebaseUser.displayName,
            photoUrl = firebaseUser.photoUrl.toString(),
            isGoogleAccount = isGoogleAccount
        )
    }
}