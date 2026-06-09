package com.ryuzora.dangani.domain.model

data class Notification(
    val id: String = "",
    val userId: String = "",
    val role: String = "requester", // "requester" or "helper"
    val type: NotificationType = NotificationType.NEW_APPLICANT,
    val title: String = "",
    val message: String = "",
    val relatedTaskId: String = "",
    val relatedTaskTitle: String = "",
    val senderName: String = "",
    val senderAvatarUrl: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val timeAgo: String
        get() {
            val diff = System.currentTimeMillis() - createdAt
            val minutes = diff / 60000
            val hours = diff / 3600000
            val days = diff / 86400000
            return when {
                minutes < 1 -> "Baru saja"
                minutes < 60 -> "$minutes mnt lalu"
                hours < 24 -> "$hours jam lalu"
                days < 7 -> "$days hari lalu"
                else -> "${days / 7} minggu lalu"
            }
        }
}
