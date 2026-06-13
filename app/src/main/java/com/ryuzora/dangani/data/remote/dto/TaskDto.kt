package com.ryuzora.dangani.data.remote.dto

import com.google.firebase.firestore.PropertyName

data class TaskDto(
    @get:PropertyName("id") @set:PropertyName("id")
    var id: String = "",
    @get:PropertyName("title") @set:PropertyName("title")
    var title: String = "",
    @get:PropertyName("description") @set:PropertyName("description")
    var description: String = "",
    @get:PropertyName("category") @set:PropertyName("category")
    var category: String = "ACADEMICS",
    @get:PropertyName("taskPoints") @set:PropertyName("taskPoints")
    var taskPoints: Int = 1,
    @get:PropertyName("status") @set:PropertyName("status")
    var status: String = "UNASSIGNED",
    @get:PropertyName("requesterId") @set:PropertyName("requesterId")
    var requesterId: String = "",
    @get:PropertyName("requesterName") @set:PropertyName("requesterName")
    var requesterName: String = "",
    @get:PropertyName("requesterAvatarUrl") @set:PropertyName("requesterAvatarUrl")
    var requesterAvatarUrl: String = "",
    @get:PropertyName("requesterIsVerified") @set:PropertyName("requesterIsVerified")
    var requesterIsVerified: Boolean = false,
    @get:PropertyName("helperId") @set:PropertyName("helperId")
    var helperId: String = "",
    @get:PropertyName("helperName") @set:PropertyName("helperName")
    var helperName: String = "",
    @get:PropertyName("helperAvatarUrl") @set:PropertyName("helperAvatarUrl")
    var helperAvatarUrl: String = "",
    @get:PropertyName("proofOfWorkUrl") @set:PropertyName("proofOfWorkUrl")
    var proofOfWorkUrl: String = "",
    @get:PropertyName("applicantCount") @set:PropertyName("applicantCount")
    var applicantCount: Int = 0,
    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var createdAt: Long = System.currentTimeMillis(),
    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt")
    var updatedAt: Long = System.currentTimeMillis()
)

