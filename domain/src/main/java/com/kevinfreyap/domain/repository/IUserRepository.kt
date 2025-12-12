package com.kevinfreyap.domain.repository

import com.kevinfreyap.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    fun getUserProfile(): Flow<User?>
}