package com.ryuzora.dangani.data.mapper

import com.ryuzora.dangani.data.local.entity.TaskEntity
import com.ryuzora.dangani.data.remote.dto.TaskDto
import com.ryuzora.dangani.domain.model.Task
import com.ryuzora.dangani.domain.model.TaskCategory
import com.ryuzora.dangani.domain.model.TaskStatus

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    category = try { TaskCategory.valueOf(category) } catch (_: Exception) { TaskCategory.ACADEMICS },
    taskPoints = taskPoints,
    status = try { TaskStatus.valueOf(status) } catch (_: Exception) { TaskStatus.UNASSIGNED },
    requesterId = requesterId,
    requesterName = requesterName,
    requesterAvatarUrl = requesterAvatarUrl,
    requesterIsVerified = requesterIsVerified,
    helperId = helperId,
    helperName = helperName,
    helperAvatarUrl = helperAvatarUrl,
    proofOfWorkUrl = proofOfWorkUrl,
    applicantCount = applicantCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    category = category.name,
    taskPoints = taskPoints,
    status = status.name,
    requesterId = requesterId,
    requesterName = requesterName,
    requesterAvatarUrl = requesterAvatarUrl,
    requesterIsVerified = requesterIsVerified,
    helperId = helperId,
    helperName = helperName,
    helperAvatarUrl = helperAvatarUrl,
    proofOfWorkUrl = proofOfWorkUrl,
    applicantCount = applicantCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun TaskDto.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    taskPoints = taskPoints,
    status = status,
    requesterId = requesterId,
    requesterName = requesterName,
    requesterAvatarUrl = requesterAvatarUrl,
    requesterIsVerified = requesterIsVerified,
    helperId = helperId,
    helperName = helperName,
    helperAvatarUrl = helperAvatarUrl,
    proofOfWorkUrl = proofOfWorkUrl,
    applicantCount = applicantCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun TaskDto.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    category = try { TaskCategory.valueOf(category) } catch (_: Exception) { TaskCategory.ACADEMICS },
    taskPoints = taskPoints,
    status = try { TaskStatus.valueOf(status) } catch (_: Exception) { TaskStatus.UNASSIGNED },
    requesterId = requesterId,
    requesterName = requesterName,
    requesterAvatarUrl = requesterAvatarUrl,
    requesterIsVerified = requesterIsVerified,
    helperId = helperId,
    helperName = helperName,
    helperAvatarUrl = helperAvatarUrl,
    proofOfWorkUrl = proofOfWorkUrl,
    applicantCount = applicantCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Task.toDto(): TaskDto = TaskDto(
    id = id,
    title = title,
    description = description,
    category = category.name,
    taskPoints = taskPoints,
    status = status.name,
    requesterId = requesterId,
    requesterName = requesterName,
    requesterAvatarUrl = requesterAvatarUrl,
    requesterIsVerified = requesterIsVerified,
    helperId = helperId,
    helperName = helperName,
    helperAvatarUrl = helperAvatarUrl,
    proofOfWorkUrl = proofOfWorkUrl,
    applicantCount = applicantCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Task.toFirestoreMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "title" to title,
    "description" to description,
    "category" to category.name,
    "taskPoints" to taskPoints,
    "status" to status.name,
    "requesterId" to requesterId,
    "requesterName" to requesterName,
    "requesterAvatarUrl" to requesterAvatarUrl,
    "requesterIsVerified" to requesterIsVerified,
    "helperId" to helperId,
    "helperName" to helperName,
    "helperAvatarUrl" to helperAvatarUrl,
    "proofOfWorkUrl" to proofOfWorkUrl,
    "applicantCount" to applicantCount,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)

