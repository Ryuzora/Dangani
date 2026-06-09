package com.ryuzora.dangani.data.remote.dto

import com.google.firebase.firestore.PropertyName

data class ReviewDto(
    @get:PropertyName("id") @set:PropertyName("id")
    var id: String = "",
    @get:PropertyName("reviewerId") @set:PropertyName("reviewerId")
    var reviewerId: String = "",
    @get:PropertyName("reviewerName") @set:PropertyName("reviewerName")
    var reviewerName: String = "",
    @get:PropertyName("reviewerAvatarUrl") @set:PropertyName("reviewerAvatarUrl")
    var reviewerAvatarUrl: String = "",
    @get:PropertyName("revieweeId") @set:PropertyName("revieweeId")
    var revieweeId: String = "",
    @get:PropertyName("taskId") @set:PropertyName("taskId")
    var taskId: String = "",
    @get:PropertyName("rating") @set:PropertyName("rating")
    var rating: Int = 5,
    @get:PropertyName("comment") @set:PropertyName("comment")
    var comment: String = "",
    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Long = System.currentTimeMillis()
)
