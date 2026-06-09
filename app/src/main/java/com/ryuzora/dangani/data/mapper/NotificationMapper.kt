package com.ryuzora.dangani.data.mapper

import com.ryuzora.dangani.data.local.entity.NotificationEntity
import com.ryuzora.dangani.data.remote.dto.NotificationDto
import com.ryuzora.dangani.domain.model.Notification
import com.ryuzora.dangani.domain.model.NotificationType

fun NotificationEntity.toDomain(): Notification = Notification(
    id = id,
    userId = userId,
    role = role,
    type = try { NotificationType.valueOf(type) } catch (_: Exception) { NotificationType.NEW_APPLICANT },
    title = title,
    message = message,
    relatedTaskId = relatedTaskId,
    relatedTaskTitle = relatedTaskTitle,
    senderName = senderName,
    senderAvatarUrl = senderAvatarUrl,
    isRead = isRead,
    createdAt = createdAt
)

fun Notification.toEntity(): NotificationEntity = NotificationEntity(
    id = id,
    userId = userId,
    role = role,
    type = type.name,
    title = title,
    message = message,
    relatedTaskId = relatedTaskId,
    relatedTaskTitle = relatedTaskTitle,
    senderName = senderName,
    senderAvatarUrl = senderAvatarUrl,
    isRead = isRead,
    createdAt = createdAt
)

fun NotificationDto.toEntity(): NotificationEntity = NotificationEntity(
    id = id,
    userId = userId,
    role = role,
    type = type,
    title = title,
    message = message,
    relatedTaskId = relatedTaskId,
    relatedTaskTitle = relatedTaskTitle,
    senderName = senderName,
    senderAvatarUrl = senderAvatarUrl,
    isRead = isRead,
    createdAt = createdAt
)

fun NotificationDto.toDomain(): Notification = Notification(
    id = id,
    userId = userId,
    role = role,
    type = try { NotificationType.valueOf(type) } catch (_: Exception) { NotificationType.NEW_APPLICANT },
    title = title,
    message = message,
    relatedTaskId = relatedTaskId,
    relatedTaskTitle = relatedTaskTitle,
    senderName = senderName,
    senderAvatarUrl = senderAvatarUrl,
    isRead = isRead,
    createdAt = createdAt
)

fun Notification.toDto(): NotificationDto = NotificationDto(
    id = id,
    userId = userId,
    role = role,
    type = type.name,
    title = title,
    message = message,
    relatedTaskId = relatedTaskId,
    relatedTaskTitle = relatedTaskTitle,
    senderName = senderName,
    senderAvatarUrl = senderAvatarUrl,
    isRead = isRead,
    createdAt = createdAt
)

fun Notification.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "userId" to userId,
    "role" to role,
    "type" to type.name,
    "title" to title,
    "message" to message,
    "relatedTaskId" to relatedTaskId,
    "relatedTaskTitle" to relatedTaskTitle,
    "senderName" to senderName,
    "senderAvatarUrl" to senderAvatarUrl,
    "isRead" to isRead,
    "createdAt" to createdAt
)
