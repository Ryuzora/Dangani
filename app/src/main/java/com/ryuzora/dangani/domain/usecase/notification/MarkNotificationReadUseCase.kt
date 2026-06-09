package com.ryuzora.dangani.domain.usecase.notification

import com.ryuzora.dangani.domain.repository.NotificationRepository

class MarkNotificationReadUseCase(private val notificationRepository: NotificationRepository) {
    suspend operator fun invoke(notificationId: String): Result<Unit> {
        return notificationRepository.markAsRead(notificationId)
    }
}
