package com.ryuzora.dangani.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_applications")
data class TaskApplicationEntity(
    @PrimaryKey
    val id: String = "",
    val taskId: String = "",
    val helperId: String = "",
    val helperName: String = "",
    val helperAvatarUrl: String = "",
    val helperRating: Double = 0.0,
    val helperTasksCompleted: Int = 0,
    val helperIsTopHelper: Boolean = false,
    val status: String = "pending",
    val appliedAt: Long = System.currentTimeMillis()
)
