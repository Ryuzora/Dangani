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
    
    private val generativeModel = GenerativeModel(
        modelName = "gemini-3.1-flash-lite",
        apiKey = BuildConfig.GEMINI_API_KEY
    )
    
    // Start a chat session to keep history
    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "user") { text("Halo! Kamu adalah asisten virtual resmi untuk Dangani, sebuah aplikasi platform peer-to-peer (P2P) untuk mahasiswa saling membantu mengerjakan tugas. Sistem Dangani terinspirasi dari metodologi Agile Scrum Jira.\\nKonteks Aplikasi:\\n- Ada 2 peran utama: 'Requester' (pembuat tugas) dan 'Helper' (pembantu tugas). Setiap pengguna bisa menjadi Requester maupun Helper tanpa perlu membuat akun terpisah.\\n- Requester membuat tugas dengan menentukan deskripsi, kategori, dan 'Task Point'. Task Point menggunakan deret Fibonacci (1, 2, 3, 5, 8, 13) yang terinspirasi dari Jira Story Points untuk merepresentasikan tingkat kesulitan/beban tugas.\\n- Tugas yang dibuat akan tampil di beranda. Pengguna lain bisa melihat detail tugas dan melamar untuk menjadi Helper.\\n- Requester dapat memilih Helper dari daftar pelamar dengan mempertimbangkan rating profil pelamar.\\n- Setelah terpilih, Helper mengerjakan tugas dan mengunggah bukti penyelesaian (Work Submission).\\n- Requester dapat menyetujui (Approve) atau meminta revisi atas hasil kerja Helper.\\n- Status Tugas memiliki 4 tahap: 'Unassigned' (Belum ditugaskan), 'In Progress' (Sedang dikerjakan), 'Need Review' (Butuh direviu), dan 'Accepted' (Diterima/Selesai).\\nTugasmu adalah menjawab pertanyaan pengguna seputar cara kerja aplikasi berdasarkan aturan di atas. Jawablah dengan ramah, sopan, singkat, solutif, dan selalu gunakan Bahasa Indonesia yang profesional.") },
            content(role = "model") { text("Mengerti! Saya adalah asisten virtual Dangani. Saya siap membantu pengguna dengan ramah, sopan, dan berbahasa Indonesia yang baik. Ada yang bisa saya bantu?") }
        )
    )

    init {
        // Add an initial greeting message
        messages.value = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                text = "Halo! Saya adalah asisten virtual Dangani. Ada yang bisa saya bantu hari ini?",
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
            text = "Mengetik...",
            isFromUser = false
        )
        messages.update { it + botMessage }

        try {
            val response = chat.sendMessage(message)
            val responseText = response.text?.trim() ?: "Maaf, saya tidak dapat merespons saat ini."

            messages.update { currentList ->
                currentList.map { 
                    if (it.id == botMessageId) it.copy(text = responseText) else it 
                }
            }
        } catch (e: Exception) {
            // Update the bot message with an error
            val errorMsg = e.message ?: e.toString()
            messages.update { currentList ->
                currentList.map { 
                    if (it.id == botMessageId) it.copy(text = "Error: $errorMsg") else it 
                }
            }
        }
    }
}