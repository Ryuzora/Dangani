package com.ryuzora.dangani.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val avatarUrl: String = "",
    val isVerified: Boolean = false,
    val totalPoints: Int = 0,
    val tasksCompleted: Int = 0,
    val ratingAverage: Double = 0.0,
    val tasksUploaded: Int = 0,
    val averageTaskPoints: Double = 0.0,
    val whatsapp: String = "",
    val instagram: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

