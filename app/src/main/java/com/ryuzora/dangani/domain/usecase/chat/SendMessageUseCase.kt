package com.ryuzora.dangani.domain.usecase.chat

import com.ryuzora.dangani.domain.repository.ChatRepository

class SendMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(message: String) {
        repository.sendMessage(message)
    }
}
