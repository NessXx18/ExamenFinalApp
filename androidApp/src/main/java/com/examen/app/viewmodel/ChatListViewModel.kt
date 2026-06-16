package com.examen.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examen.app.models.ChatSession
import com.examen.app.models.CreateSessionRequest
import com.examen.app.network.ApiClient
import com.examen.app.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


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
        viewModelScope.launch {
            try {
                val response = apiService.createSession(
                    CreateSessionRequest(title = "Nueva conversación")
                )

                if (response.isSuccessful && response.body() != null) {
                    val nuevaSesion = response.body()!!

                    // Se conserva la lista actual (si existe) y se antepone la nueva sesión.
                    val listaActual = (_uiState.value as? ChatListUiState.Success)?.sessions ?: emptyList()
                    _uiState.value = ChatListUiState.Success(listOf(nuevaSesion) + listaActual)

                    onCreated(nuevaSesion)
                } else {
                    _uiState.value = ChatListUiState.Error(
                        "Error ${response.code()}: no se pudo crear la conversación"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ChatListUiState.Error(
                    e.message ?: "Error de conexión con el servidor"
                )
            }
        }
    }
}