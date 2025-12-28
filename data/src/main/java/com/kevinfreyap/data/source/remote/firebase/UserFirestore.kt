package com.kevinfreyap.data.source.remote.firebase

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserFirestore(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val isGoogleAccount: Boolean = false,
    val currency: String = "IDR",
    @ServerTimestamp
    val lastLogin: Date? = null,
    @ServerTimestamp
    val createdAt: Date? = null
)
