package com.ryuzora.dangani.domain.model

data class Review(
    val id: String = "",
    val reviewerId: String = "",
    val reviewerName: String = "",
    val reviewerAvatarUrl: String = "",
    val revieweeId: String = "",
    val taskId: String = "",
    val rating: Int = 5, // 1-5 stars
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val timeAgo: String
        get() {
            val diff = System.currentTimeMillis() - createdAt
            val days = diff / 86400000
            val weeks = days / 7
            return when {
                days < 1 -> "Hari ini"
                days < 7 -> "$days hari lalu"
                weeks < 4 -> "$weeks minggu lalu"
                else -> "${days / 30} bulan lalu"
            }
        }
}

