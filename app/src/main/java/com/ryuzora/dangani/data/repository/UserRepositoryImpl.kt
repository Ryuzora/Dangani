package com.ryuzora.dangani.data.repository

import android.net.Uri
import com.ryuzora.dangani.data.local.dao.UserDao
import com.ryuzora.dangani.data.mapper.toDomain
import com.ryuzora.dangani.data.mapper.toEntity
import com.ryuzora.dangani.data.mapper.toFirestoreMap
import com.ryuzora.dangani.data.remote.FirebaseAuthService
import com.ryuzora.dangani.data.remote.FirebaseStorageService
import com.ryuzora.dangani.data.remote.SupabaseStorageService
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.remote.dto.UserDto
import com.ryuzora.dangani.domain.model.User
import com.ryuzora.dangani.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreService,
    private val storageService: FirebaseStorageService
) : UserRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun getCurrentUserId(): String? = authService.getCurrentUserId()

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = authService.login(email, password)
            val firebaseUser = authResult.getOrThrow()
            val userId = firebaseUser.uid

            // Fetch user data from Firestore
            val doc = firestoreService.getDocument("users", userId)
            if (doc != null && doc.exists()) {
                val userDto = doc.toObject(UserDto::class.java)
                if (userDto != null) {
                    userDto.id = userId
                    val entity = userDto.toEntity()
                    userDao.insert(entity)
                    Result.success(entity.toDomain())
                } else {
                    Result.failure(Exception("Failed to parse user data"))
                }
            } else {
                // User exists in Auth but not in Firestore — create basic profile
                val newUser = User(
                    id = userId,
                    email = email,
                    username = email.substringBefore("@")
                )
                firestoreService.setDocument("users", userId, newUser.toFirestoreMap())
                userDao.insert(newUser.toEntity())
                Result.success(newUser)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, username: String, password: String): Result<User> {
        return try {
            val authResult = authService.register(email, password)
            val firebaseUser = authResult.getOrThrow()
            val userId = firebaseUser.uid

            val newUser = User(
                id = userId,
                email = email,
                username = username,
                createdAt = System.currentTimeMillis()
            )

            // Create Firestore document
            firestoreService.setDocument("users", userId, newUser.toFirestoreMap())

            // Save to Room
            userDao.insert(newUser.toEntity())

            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        authService.logout()
    }

    override fun getUserProfile(userId: String): Flow<User?> {
        // Emit from Room, refresh from Firestore in background
        coroutineScope.launch {
            try {
                val doc = firestoreService.getDocument("users", userId)
                if (doc != null && doc.exists()) {
                    val userDto = doc.toObject(UserDto::class.java)
                    if (userDto != null) {
                        userDto.id = userId
                        userDao.insert(userDto.toEntity())
                    }
                }
            } catch (_: Exception) {
                // Silently fail — Room cache will be used
            }
        }
        return userDao.getById(userId).map { it?.toDomain() }
    }

    override fun getCurrentUserProfile(): Flow<User?> {
        val userId = getCurrentUserId() ?: return kotlinx.coroutines.flow.flowOf(null)
        return getUserProfile(userId)
    }

    override suspend fun updateProfile(user: User): Result<Unit> {
        return try {
            firestoreService.updateDocument("users", user.id, user.toFirestoreMap())
            userDao.update(user.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfilePhoto(userId: String, imageUri: String): Result<String> {
        return try {
            val path = "$userId/${System.currentTimeMillis()}.jpg"
            val supabaseService = SupabaseStorageService()
            val uploadResult = supabaseService.uploadFile(
                bucket = "avatar",
                path = path,
                fileUri = Uri.parse(imageUri)
            )
            val downloadUrl = uploadResult.getOrThrow()

            // Update Firestore
            firestoreService.updateDocument("users", userId, mapOf("avatarUrl" to downloadUrl))

            // Update Room
            val doc = firestoreService.getDocument("users", userId)
            if (doc != null && doc.exists()) {
                val userDto = doc.toObject(UserDto::class.java)
                if (userDto != null) {
                    userDto.id = userId
                    userDao.insert(userDto.toEntity())
                }
            }

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
