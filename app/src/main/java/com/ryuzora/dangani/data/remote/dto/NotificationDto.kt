package com.ryuzora.dangani.data.remote.dto

import com.google.firebase.firestore.PropertyName

data class NotificationDto(
    @get:PropertyName("id") @set:PropertyName("id")
    var id: String = "",
    @get:PropertyName("userId") @set:PropertyName("userId")
    var userId: String = "",
    @get:PropertyName("role") @set:PropertyName("role")
    var role: String = "requester",
    @get:PropertyName("type") @set:PropertyName("type")
    var type: String = "NEW_APPLICANT",
    @get:PropertyName("title") @set:PropertyName("title")
    var title: String = "",
    @get:PropertyName("message") @set:PropertyName("message")
    var message: String = "",
    @get:PropertyName("relatedTaskId") @set:PropertyName("relatedTaskId")
    var relatedTaskId: String = "",
    @get:PropertyName("relatedTaskTitle") @set:PropertyName("relatedTaskTitle")
    var relatedTaskTitle: String = "",
    @get:PropertyName("senderName") @set:PropertyName("senderName")
    var senderName: String = "",
    @get:PropertyName("senderAvatarUrl") @set:PropertyName("senderAvatarUrl")
    var senderAvatarUrl: String = "",
    @get:PropertyName("isRead") @set:PropertyName("isRead")
    var isRead: Boolean = false,
    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Long = System.currentTimeMillis()
)
