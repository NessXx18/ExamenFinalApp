package org.inovaapp.project.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(val name: String, val email: String, val password: String)

@Serializable
data class UserDto(val id: String, val name: String, val email: String)

@Serializable
data class AuthResponse(val token: String, val user: UserDto)

@Serializable
data class ChatSession(
    val id: String,
    val title: String,
    val lastMessage: String = "",
    val updatedAt: String = "",
    val messageCount: Int = 0
)

@Serializable
data class Message(
    val id: String,
    val role: String,
    val content: String,
    val hasRiskSignal: Boolean = false,
    val timestamp: String = ""
)

@Serializable
data class SendMessageRequest(val message: String)

@Serializable
data class CreateSessionRequest(val title: String)

@Serializable
data class SendMessageResponse(
    val userMessage: Message,
    val aiMessage: Message
)