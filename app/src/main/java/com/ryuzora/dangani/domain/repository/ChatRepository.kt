package com.ryuzora.dangani.domain.repository

import com.ryuzora.dangani.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(): Flow<List<ChatMessage>>
    suspend fun sendMessage(message: String)
}
