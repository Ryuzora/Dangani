package com.ryuzora.dangani.data.remote.dto

import com.google.firebase.firestore.PropertyName

data class UserDto(
    @get:PropertyName("id") @set:PropertyName("id")
    var id: String = "",
    @get:PropertyName("email") @set:PropertyName("email")
    var email: String = "",
    @get:PropertyName("username") @set:PropertyName("username")
    var username: String = "",
    @get:PropertyName("avatarUrl") @set:PropertyName("avatarUrl")
    var avatarUrl: String = "",
    @get:PropertyName("isVerified") @set:PropertyName("isVerified")
    var isVerified: Boolean = false,
    @get:PropertyName("totalPoints") @set:PropertyName("totalPoints")
    var totalPoints: Int = 0,
    @get:PropertyName("tasksCompleted") @set:PropertyName("tasksCompleted")
    var tasksCompleted: Int = 0,
    @get:PropertyName("ratingAverage") @set:PropertyName("ratingAverage")
    var ratingAverage: Double = 0.0,
    @get:PropertyName("tasksUploaded") @set:PropertyName("tasksUploaded")
    var tasksUploaded: Int = 0,
    @get:PropertyName("averageTaskPoints") @set:PropertyName("averageTaskPoints")
    var averageTaskPoints: Double = 0.0,
    @get:PropertyName("whatsapp") @set:PropertyName("whatsapp")
    var whatsapp: String = "",
    @get:PropertyName("instagram") @set:PropertyName("instagram")
    var instagram: String = "",
    @get:PropertyName("fcmToken") @set:PropertyName("fcmToken")
    var fcmToken: String = "",
    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Long = System.currentTimeMillis()
)
