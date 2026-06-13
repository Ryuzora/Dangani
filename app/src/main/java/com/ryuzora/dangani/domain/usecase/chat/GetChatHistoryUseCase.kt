package com.ryuzora.dangani.domain.usecase.chat

import com.ryuzora.dangani.domain.model.ChatMessage
import com.ryuzora.dangani.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetChatHistoryUseCase(private val repository: ChatRepository) {
    operator fun invoke(): Flow<List<ChatMessage>> {
        return repository.getMessages()
    }
}
