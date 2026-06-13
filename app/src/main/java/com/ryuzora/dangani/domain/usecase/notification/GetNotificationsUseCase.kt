package com.ryuzora.dangani.domain.usecase.notification

import com.ryuzora.dangani.domain.model.Notification
import com.ryuzora.dangani.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

class GetNotificationsUseCase(private val notificationRepository: NotificationRepository) {
    operator fun invoke(userId: String, role: String): Flow<List<Notification>> {
        return notificationRepository.getNotifications(userId, role)
    }
}

