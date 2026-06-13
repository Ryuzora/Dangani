package com.ryuzora.dangani.domain.model

data class TaskApplication(
    val id: String = "",
    val taskId: String = "",
    val helperId: String = "",
    val helperName: String = "",
    val helperAvatarUrl: String = "",
    val helperRating: Double = 0.0,
    val helperTasksCompleted: Int = 0,
    val helperIsTopHelper: Boolean = false,
    val status: String = "pending", // pending, accepted, rejected
    val appliedAt: Long = System.currentTimeMillis()
)

