package com.ryuzora.dangani.data.local.entity

import androidx.room.Entity

@Entity(tableName = "favorites", primaryKeys = ["userId", "taskId"])
data class FavoriteEntity(
    val userId: String = "",
    val taskId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
