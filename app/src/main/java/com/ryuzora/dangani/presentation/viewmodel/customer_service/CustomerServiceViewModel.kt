package com.ryuzora.dangani.presentation.viewmodel.customer_service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryuzora.dangani.domain.model.ChatMessage
import com.ryuzora.dangani.domain.usecase.chat.GetChatHistoryUseCase
import com.ryuzora.dangani.domain.usecase.chat.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CustomerServiceState(
    val messages: List<ChatMessage> = emptyList(),
    val isSending: Boolean = false
)

class CustomerServiceViewModel(
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomerServiceState())
    val uiState: StateFlow<CustomerServiceState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getChatHistoryUseCase().collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    fun sendMessage(message: String) {
        if (message.isBlank() || _uiState.value.isSending) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true) }
            sendMessageUseCase(message.trim())
            _uiState.update { it.copy(isSending = false) }
        }
    }
}
