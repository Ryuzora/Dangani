package com.ryuzora.dangani.data.remote.dto

import com.google.firebase.firestore.PropertyName

data class TaskApplicationDto(
    @get:PropertyName("id") @set:PropertyName("id")
    var id: String = "",
    @get:PropertyName("taskId") @set:PropertyName("taskId")
    var taskId: String = "",
    @get:PropertyName("helperId") @set:PropertyName("helperId")
    var helperId: String = "",
    @get:PropertyName("helperName") @set:PropertyName("helperName")
    var helperName: String = "",
    @get:PropertyName("helperAvatarUrl") @set:PropertyName("helperAvatarUrl")
    var helperAvatarUrl: String = "",
    @get:PropertyName("helperRating") @set:PropertyName("helperRating")
    var helperRating: Double = 0.0,
    @get:PropertyName("helperTasksCompleted") @set:PropertyName("helperTasksCompleted")
    var helperTasksCompleted: Int = 0,
    @get:PropertyName("helperIsTopHelper") @set:PropertyName("helperIsTopHelper")
    var helperIsTopHelper: Boolean = false,
    @get:PropertyName("status") @set:PropertyName("status")
    var status: String = "pending",
    @get:PropertyName("appliedAt") @set:PropertyName("appliedAt")
    var appliedAt: Long = System.currentTimeMillis()
)

