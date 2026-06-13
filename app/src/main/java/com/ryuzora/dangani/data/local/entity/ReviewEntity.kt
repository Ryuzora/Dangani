package com.ryuzora.dangani.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey
    val id: String = "",
    val reviewerId: String = "",
    val reviewerName: String = "",
    val reviewerAvatarUrl: String = "",
    val revieweeId: String = "",
    val taskId: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

