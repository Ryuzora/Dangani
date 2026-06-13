package com.ryuzora.dangani.data.mapper

import com.ryuzora.dangani.data.local.entity.UserEntity
import com.ryuzora.dangani.data.remote.dto.UserDto
import com.ryuzora.dangani.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    email = email,
    username = username,
    avatarUrl = avatarUrl,
    isVerified = isVerified,
    totalPoints = totalPoints,
    tasksCompleted = tasksCompleted,
    ratingAverage = ratingAverage,
    tasksUploaded = tasksUploaded,
    averageTaskPoints = averageTaskPoints,
    whatsapp = whatsapp,
    instagram = instagram,
    createdAt = createdAt
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    username = username,
    avatarUrl = avatarUrl,
    isVerified = isVerified,
    totalPoints = totalPoints,
    tasksCompleted = tasksCompleted,
    ratingAverage = ratingAverage,
    tasksUploaded = tasksUploaded,
    averageTaskPoints = averageTaskPoints,
    whatsapp = whatsapp,
    instagram = instagram,
    createdAt = createdAt
)

fun UserDto.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    username = username,
    avatarUrl = avatarUrl,
    isVerified = isVerified,
    totalPoints = totalPoints,
    tasksCompleted = tasksCompleted,
    ratingAverage = ratingAverage,
    tasksUploaded = tasksUploaded,
    averageTaskPoints = averageTaskPoints,
    whatsapp = whatsapp,
    instagram = instagram,
    createdAt = createdAt
)

fun UserDto.toDomain(): User = User(
    id = id,
    email = email,
    username = username,
    avatarUrl = avatarUrl,
    isVerified = isVerified,
    totalPoints = totalPoints,
    tasksCompleted = tasksCompleted,
    ratingAverage = ratingAverage,
    tasksUploaded = tasksUploaded,
    averageTaskPoints = averageTaskPoints,
    whatsapp = whatsapp,
    instagram = instagram,
    createdAt = createdAt
)

fun User.toDto(): UserDto = UserDto(
    id = id,
    email = email,
    username = username,
    avatarUrl = avatarUrl,
    isVerified = isVerified,
    totalPoints = totalPoints,
    tasksCompleted = tasksCompleted,
    ratingAverage = ratingAverage,
    tasksUploaded = tasksUploaded,
    averageTaskPoints = averageTaskPoints,
    whatsapp = whatsapp,
    instagram = instagram,
    createdAt = createdAt
)

fun User.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "email" to email,
    "username" to username,
    "avatarUrl" to avatarUrl,
    "isVerified" to isVerified,
    "totalPoints" to totalPoints,
    "tasksCompleted" to tasksCompleted,
    "ratingAverage" to ratingAverage,
    "tasksUploaded" to tasksUploaded,
    "averageTaskPoints" to averageTaskPoints,
    "whatsapp" to whatsapp,
    "instagram" to instagram,
    "createdAt" to createdAt
)

