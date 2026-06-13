package com.ryuzora.dangani.data.repository

import com.ryuzora.dangani.data.local.dao.NotificationDao
import com.ryuzora.dangani.data.mapper.toDomain
import com.ryuzora.dangani.data.mapper.toEntity
import com.ryuzora.dangani.data.mapper.toFirestoreMap
import com.ryuzora.dangani.data.remote.FirestoreService
import com.ryuzora.dangani.data.remote.dto.NotificationDto
import com.ryuzora.dangani.domain.model.Notification
import com.ryuzora.dangani.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NotificationRepositoryImpl(
    private val notificationDao: NotificationDao,
    private val firestoreService: FirestoreService
) : NotificationRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun getNotifications(userId: String, role: String): Flow<List<Notification>> {
        // Sync from Firestore real-time listener in background
        coroutineScope.launch {
            try {
                firestoreService.queryCollectionTwoFields(
                    "notifications", "userId", userId, "role", role
                ).collect { docs ->
                    val entities = docs.mapNotNull { doc ->
                        val dto = doc.toObject(NotificationDto::class.java)
                        dto?.apply { id = doc.id }?.toEntity()
                    }
                    notificationDao.insertAll(entities)
                }
            } catch (_: Exception) { }
        }
        return notificationDao.getByUserIdAndRole(userId, role)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getUnreadCount(userId: String, role: String): Flow<Int> {
        return notificationDao.getUnreadCount(userId, role)
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            // Update Firestore
            firestoreService.updateDocument(
                "notifications", notificationId, mapOf("isRead" to true)
            )
            // Update Room
            notificationDao.markAsRead(notificationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createNotification(notification: Notification): Result<Unit> {
        return try {
            val notifMap = notification.toFirestoreMap().toMutableMap()
            val docId = firestoreService.addDocument("notifications", notifMap)
            firestoreService.updateDocument("notifications", docId, mapOf("id" to docId))

            val savedNotification = notification.copy(id = docId)
            notificationDao.insert(savedNotification.toEntity())

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

