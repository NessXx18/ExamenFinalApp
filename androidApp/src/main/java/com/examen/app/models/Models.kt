package com.examen.app.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String
)

data class ChatSession(
    val id: String,
    val title: String,
    val createdAt: String,
    val lastMessage: String?
)

data class Message(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String,
    val hasRiskSignal: Boolean = false
)

data class ApiMessage(
    val id: String,
    val role: String,
    val content: String,
    val hasRiskSignal: Boolean? = null,
    val timestamp: String
) {
    fun toMessage(): Message = Message(
        id = id,
        text = content,
        isFromUser = role == "user",
        timestamp = timestamp,
        hasRiskSignal = hasRiskSignal == true
    )
}

data class SendMessageRequest(
    val message: String
)

data class SendMessageResponse(
    @SerializedName("userMessage")
    val userMessage: ApiMessage,
    @SerializedName("aiMessage")
    val aiMessage: ApiMessage,
    val sessionId: String
)
