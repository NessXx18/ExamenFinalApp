package com.examen.app.network

import com.examen.app.models.AuthResponse
import com.examen.app.models.ApiMessage
import com.examen.app.models.ChatSession
import com.examen.app.models.LoginRequest
import com.examen.app.models.RegisterRequest
import com.examen.app.models.SendMessageRequest
import com.examen.app.models.SendMessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("chats/sessions")
    suspend fun getSessions(): Response<List<ChatSession>>

    @GET("chats/sessions/{sessionId}/messages")
    suspend fun getMessages(
        @Path("sessionId") sessionId: String
    ): Response<List<ApiMessage>>

    @POST("chats/sessions/{sessionId}/messages")
    suspend fun sendMessage(
        @Path("sessionId") sessionId: String,
        @Body request: SendMessageRequest
    ): Response<SendMessageResponse>
}
