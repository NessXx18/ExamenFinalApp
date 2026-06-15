package com.examen.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examen.app.models.ChatSession
import com.examen.app.network.ApiClient
import com.examen.app.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


sealed interface ChatListUiState {
    data object Loading : ChatListUiState

    data class Success(val sessions: List<ChatSession>) : ChatListUiState

    data class Error(val message: String) : ChatListUiState
}


class ChatListViewModel : ViewModel() {

    private val apiService: ApiService =
        ApiClient.retrofit.create(ApiService::class.java)

    private val _uiState = MutableStateFlow<ChatListUiState>(ChatListUiState.Loading)

    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    init {
        fetchSessions()
    }

    
    fun fetchSessions() {
        viewModelScope.launch {
            _uiState.value = ChatListUiState.Loading
            try {
                val response = apiService.getSessions()
                if (response.isSuccessful) {
                    val sessions = response.body() ?: emptyList()
                    _uiState.value = ChatListUiState.Success(sessions)
                } else {
                    _uiState.value = ChatListUiState.Error(
                        "Error ${response.code()}: no se pudieron cargar las conversaciones"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ChatListUiState.Error(
                    e.message ?: "Error de conexión con el servidor"
                )
            }
        }
    }

    
    fun createNewSession(onCreated: (ChatSession) -> Unit = {}) {
        val nuevaSesion = ChatSession(
            id = UUID.randomUUID().toString(),
            title = "Nueva conversación",
            createdAt = currentDate(),
            lastMessage = null
        )

        // Se conserva la lista actual (si existe) y se antepone la nueva sesión.
        val listaActual = (_uiState.value as? ChatListUiState.Success)?.sessions ?: emptyList()
        _uiState.value = ChatListUiState.Success(listOf(nuevaSesion) + listaActual)

        onCreated(nuevaSesion)
    }

   
    private fun currentDate(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return formato.format(Date())
    }
}