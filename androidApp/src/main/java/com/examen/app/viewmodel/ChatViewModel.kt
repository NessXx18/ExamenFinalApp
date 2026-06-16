package com.examen.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.examen.app.models.Message
import com.examen.app.models.SendMessageRequest
import com.examen.app.network.ApiClient
import com.examen.app.network.ApiService
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatViewModel(
    private val sessionId: String
) : ViewModel() {

    private val apiService: ApiService =
        ApiClient.retrofit.create(ApiService::class.java)

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchMessages()
    }

    fun fetchMessages() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = apiService.getMessages(sessionId)
                if (response.isSuccessful) {
                    _messages.value = response.body()
                        ?.map { it.toMessage() }
                        ?: emptyList()
                } else {
                    _errorMessage.value =
                        "Error ${response.code()}: no se pudieron cargar los mensajes"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error de conexion con el servidor"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(text: String) {
        val trimmedText = text.trim()
        if (trimmedText.isEmpty() || _isTyping.value) return

        val temporaryMessage = Message(
            id = UUID.randomUUID().toString(),
            text = trimmedText,
            isFromUser = true,
            timestamp = currentDate()
        )

        _messages.value = _messages.value + temporaryMessage
        _isTyping.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = apiService.sendMessage(
                    sessionId = sessionId,
                    request = SendMessageRequest(trimmedText)
                )

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    val userMessage = responseBody.userMessage.toMessage()
                    val aiMessage = responseBody.aiMessage.toMessage()
                    val currentMessages = _messages.value
                        .filterNot { it.id == temporaryMessage.id }

                    _messages.value = currentMessages + userMessage + aiMessage
                } else {
                    _errorMessage.value =
                        "Error ${response.code()}: no se pudo enviar el mensaje"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error de conexion con el servidor"
            } finally {
                _isTyping.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun currentDate(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return format.format(Date())
    }

    class Factory(
        private val sessionId: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                return ChatViewModel(sessionId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
