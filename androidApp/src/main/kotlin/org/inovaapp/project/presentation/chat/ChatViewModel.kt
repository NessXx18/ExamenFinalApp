package org.inovaapp.project.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.inovaapp.project.data.api.ApiService
import org.inovaapp.project.data.models.Message

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isTyping: Boolean = false,
    val error: String? = null
)

class ChatViewModel(private val sessionId: String) : ViewModel() {
    private val _state = MutableStateFlow(ChatUiState())
    val state = _state.asStateFlow()

    init {
        loadMessages()
    }

    fun loadMessages() = viewModelScope.launch {
        _state.value = _state.value.copy(isLoading = true)
        val result = ApiService.getMessages(sessionId)
        result.fold(
            onSuccess = { messages ->
                _state.value = _state.value.copy(
                    messages = messages,
                    isLoading = false
                )
            },
            onFailure = {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "No se pudieron cargar los mensajes"
                )
            }
        )
    }

    fun onInputChange(text: String) {
        _state.value = _state.value.copy(inputText = text)
    }

    fun sendMessage() = viewModelScope.launch {
        val text = _state.value.inputText.trim()
        if (text.isBlank()) return@launch

        _state.value = _state.value.copy(
            inputText = "",
            isTyping = true,
            error = null
        )

        val result = ApiService.sendMessage(sessionId, text)
        result.fold(
            onSuccess = { response ->
                _state.value = _state.value.copy(
                    messages = _state.value.messages + listOf(
                        response.userMessage,
                        response.aiMessage
                    ),
                    isTyping = false
                )
            },
            onFailure = {
                _state.value = _state.value.copy(
                    isTyping = false,
                    error = "No se pudo enviar el mensaje"
                )
            }
        )
    }
}

class ChatViewModelFactory(private val sessionId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ChatViewModel(sessionId) as T
    }
}