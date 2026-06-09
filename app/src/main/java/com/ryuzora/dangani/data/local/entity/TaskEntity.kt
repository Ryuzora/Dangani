package com.ryuzora.dangani.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "ACADEMICS",
    val taskPoints: Int = 1,
    val status: String = "UNASSIGNED",
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
)
