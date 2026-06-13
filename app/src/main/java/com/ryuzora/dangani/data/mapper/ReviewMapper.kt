package com.ryuzora.dangani.data.mapper

import com.ryuzora.dangani.data.local.entity.ReviewEntity
import com.ryuzora.dangani.data.remote.dto.ReviewDto
import com.ryuzora.dangani.domain.model.Review

fun ReviewEntity.toDomain(): Review = Review(
    id = id,
    reviewerId = reviewerId,
    reviewerName = reviewerName,
    reviewerAvatarUrl = reviewerAvatarUrl,
    revieweeId = revieweeId,
    taskId = taskId,
    rating = rating,
    comment = comment,
    createdAt = createdAt
)

fun Review.toEntity(): ReviewEntity = ReviewEntity(
    id = id,
    reviewerId = reviewerId,
    reviewerName = reviewerName,
    reviewerAvatarUrl = reviewerAvatarUrl,
    revieweeId = revieweeId,
    taskId = taskId,
    rating = rating,
    comment = comment,
    createdAt = createdAt
)

fun ReviewDto.toEntity(): ReviewEntity = ReviewEntity(
    id = id,
    reviewerId = reviewerId,
    reviewerName = reviewerName,
    reviewerAvatarUrl = reviewerAvatarUrl,
    revieweeId = revieweeId,
    taskId = taskId,
    rating = rating,
    comment = comment,
    createdAt = createdAt
)

fun ReviewDto.toDomain(): Review = Review(
    id = id,
    reviewerId = reviewerId,
    reviewerName = reviewerName,
    reviewerAvatarUrl = reviewerAvatarUrl,
    revieweeId = revieweeId,
    taskId = taskId,
    rating = rating,
    comment = comment,
    createdAt = createdAt
)

fun Review.toDto(): ReviewDto = ReviewDto(
    id = id,
    reviewerId = reviewerId,
    reviewerName = reviewerName,
    reviewerAvatarUrl = reviewerAvatarUrl,
    revieweeId = revieweeId,
    taskId = taskId,
    rating = rating,
    comment = comment,
    createdAt = createdAt
)

fun Review.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "reviewerId" to reviewerId,
    "reviewerName" to reviewerName,
    "reviewerAvatarUrl" to reviewerAvatarUrl,
    "revieweeId" to revieweeId,
    "taskId" to taskId,
    "rating" to rating,
    "comment" to comment,
    "createdAt" to createdAt
)

