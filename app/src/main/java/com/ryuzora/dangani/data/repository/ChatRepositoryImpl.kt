package com.ryuzora.dangani.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.ryuzora.dangani.BuildConfig
import com.ryuzora.dangani.domain.model.ChatMessage
import com.ryuzora.dangani.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class ChatRepositoryImpl : ChatRepository {

    private val messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    
    // Initialize Gemini model
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )
    
    // Start a chat session to keep history
    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "user") { text("Hello! You are the Dangani virtual assistant, a helpful customer service AI for the Dangani app. Be polite, concise, and helpful.") },
            content(role = "model") { text("Understood! I am the Dangani virtual assistant. I will assist the user politely and concisely.") }
        )
    )

    init {
        // Add an initial greeting message
        messages.value = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                text = "Hello! I am the Dangani virtual assistant. How can I help you today?",
                isFromUser = false
            )
        )
    }

    override fun getMessages(): Flow<List<ChatMessage>> = messages

    override suspend fun sendMessage(message: String) {
        // 1. Add user message
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = message,
            isFromUser = true
        )
        messages.update { it + userMessage }

        // 2. Add empty/typing bot message
        val botMessageId = UUID.randomUUID().toString()
        val botMessage = ChatMessage(
            id = botMessageId,
            text = "Typing...",
            isFromUser = false
        )
        messages.update { it + botMessage }

        try {
            val response = chat.sendMessage(message)
            val responseText = response.text?.trim() ?: "I'm sorry, I couldn't generate a response."

            messages.update { currentList ->
                currentList.map { 
                    if (it.id == botMessageId) it.copy(text = responseText) else it 
                }
            }
        } catch (e: Exception) {
            messages.update { currentList ->
                currentList.map { 
                    if (it.id == botMessageId) it.copy(text = "I'm sorry, something went wrong. Please verify your GEMINI_API_KEY in local.properties.") else it 
                }
            }
        }
    }
}