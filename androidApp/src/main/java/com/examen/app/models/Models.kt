package com.examen.app.models

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
    val timestamp: String
)
