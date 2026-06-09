package com.ryuzora.dangani.domain.repository

import com.ryuzora.dangani.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(userId: String, role: String): Flow<List<Notification>>
    fun getUnreadCount(userId: String, role: String): Flow<Int>
    suspend fun markAsRead(notificationId: String): Result<Unit>
    suspend fun createNotification(notification: Notification): Result<Unit>
}
