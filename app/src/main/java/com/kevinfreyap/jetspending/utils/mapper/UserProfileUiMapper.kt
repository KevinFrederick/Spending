package com.kevinfreyap.jetspending.utils.mapper

import com.kevinfreyap.domain.model.User
import com.kevinfreyap.jetspending.ui.model.UserProfileUi
import javax.inject.Inject

class UserProfileUiMapper @Inject constructor() {
    fun mapUserProfileDomainToUi(domain: User): UserProfileUi {
        return UserProfileUi(
            imageUrl = domain.photoUrl,
            displayName = domain.displayName,
            displayEmail = domain.email
        )
    }
}