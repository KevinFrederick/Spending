package com.kevinfreyap.data.mapper

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.kevinfreyap.data.source.remote.firebase.UserFirestore
import com.kevinfreyap.domain.model.AppCurrency
import com.kevinfreyap.domain.model.User
import javax.inject.Inject

class UserMapper @Inject constructor() {
    fun mapFirebaseUserToUser(firebaseUser: UserFirestore): User {
        return User(
            uid = firebaseUser.uid,
            email = firebaseUser.email,
            displayName = firebaseUser.name,
            photoUrl = firebaseUser.photoUrl,
            isGoogleAccount = firebaseUser.isGoogleAccount,
            currency = AppCurrency.valueOf(firebaseUser.currency)
        )
    }
    
    fun mapFirebaseUserToFirestoreUser(firebaseUser: FirebaseUser): UserFirestore {
        val isGoogleAccount = firebaseUser.providerData.any {userInfo -> 
            userInfo.providerId == GoogleAuthProvider.PROVIDER_ID
        }
        
        return UserFirestore(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            name = firebaseUser.displayName ?: "",
            photoUrl = firebaseUser.photoUrl.toString(),
            isGoogleAccount = isGoogleAccount,
        )
    }
}