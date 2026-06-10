package org.inovaapp.project.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.inovaapp.project.data.api.ApiClient
import org.inovaapp.project.data.api.ApiService

data class LoginUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    fun onNameChange(v: String) { _state.value = _state.value.copy(name = v) }
    fun onEmailChange(v: String) { _state.value = _state.value.copy(email = v) }
    fun onPasswordChange(v: String) { _state.value = _state.value.copy(password = v) }

    fun login() = viewModelScope.launch {
        _state.value = _state.value.copy(isLoading = true, error = null)
        val result = ApiService.login(_state.value.email, _state.value.password)
        result.fold(
            onSuccess = { auth ->
                ApiClient.token = auth.token
                _state.value = _state.value.copy(isLoading = false, isSuccess = true)
            },
            onFailure = {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Correo o contraseña incorrectos"
                )
            }
        )
    }

    fun register() = viewModelScope.launch {
        if (_state.value.name.isBlank()) {
            _state.value = _state.value.copy(error = "El nombre es requerido")
            return@launch
        }
        _state.value = _state.value.copy(isLoading = true, error = null)
        val result = ApiService.register(
            _state.value.name,
            _state.value.email,
            _state.value.password
        )
        result.fold(
            onSuccess = { auth ->
                ApiClient.token = auth.token
                _state.value = _state.value.copy(isLoading = false, isSuccess = true)
            },
            onFailure = {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "No se pudo crear la cuenta"
                )
            }
        )
    }
}