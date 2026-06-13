package com.ryuzora.dangani.domain.model

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: TaskCategory = TaskCategory.ACADEMICS,
    val taskPoints: Int = 1,
    val status: TaskStatus = TaskStatus.UNASSIGNED,
    val requesterId: String = "",
    val requesterName: String = "",
    val requesterAvatarUrl: String = "",
    val requesterIsVerified: Boolean = false,
    val helperId: String = "",
    val helperName: String = "",
    val helperAvatarUrl: String = "",
    val proofOfWorkUrl: String = "",
    val applicantCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isEditable: Boolean
        get() = proofOfWorkUrl.isEmpty()

    val isDeletable: Boolean
        get() = proofOfWorkUrl.isEmpty()

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

    val updatedTimeAgo: String
        get() {
            val diff = System.currentTimeMillis() - updatedAt
            val minutes = diff / 60000
            val hours = diff / 3600000
            val days = diff / 86400000
            return when {
                minutes < 1 -> "Baru saja"
                minutes < 60 -> "$minutes menit lalu"
                hours < 24 -> "$hours jam lalu"
                days < 7 -> "$days hari lalu"
                else -> "${days / 7} minggu lalu"
            }
        }
}

