package com.ryuzora.dangani.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val role: String = "requester",
    val type: String = "NEW_APPLICANT",
    val title: String = "",
    val message: String = "",
    val relatedTaskId: String = "",
    val relatedTaskTitle: String = "",
    val senderName: String = "",
    val senderAvatarUrl: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
