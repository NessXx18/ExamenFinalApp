package org.inovaapp.project.presentation.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.inovaapp.project.data.api.ApiService
import org.inovaapp.project.data.models.ChatSession

data class ChatListUiState(
    val sessions: List<ChatSession> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatListViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChatListUiState())
    val state = _state.asStateFlow()

    init {
        loadSessions()
    }

    fun loadSessions() = viewModelScope.launch {
        _state.value = _state.value.copy(isLoading = true, error = null)
        val result = ApiService.getSessions()
        result.fold(
            onSuccess = { sessions ->
                _state.value = _state.value.copy(
                    sessions = sessions,
                    isLoading = false
                )
            },
            onFailure = {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "No se pudieron cargar las conversaciones"
                )
            }
        )
    }

    fun createSession() = viewModelScope.launch {
        val title = "Sesión ${_state.value.sessions.size + 1}"
        val result = ApiService.createSession(title)
        result.fold(
            onSuccess = { loadSessions() },
            onFailure = {
                _state.value = _state.value.copy(
                    error = "No se pudo crear la sesión"
                )
            }
        )
    }
}