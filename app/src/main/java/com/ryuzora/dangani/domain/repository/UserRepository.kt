package com.ryuzora.dangani.domain.repository

import com.ryuzora.dangani.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUserId(): String?
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, username: String, password: String): Result<User>
    suspend fun logout()
    fun getUserProfile(userId: String): Flow<User?>
    fun getCurrentUserProfile(): Flow<User?>
    suspend fun updateProfile(user: User): Result<Unit>
    suspend fun uploadProfilePhoto(userId: String, imageUri: String): Result<String>
}

