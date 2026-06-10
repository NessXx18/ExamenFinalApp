package org.inovaapp.project.data.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.inovaapp.project.data.models.*

object ApiService {

    private fun HttpRequestBuilder.withAuth() {
        ApiClient.token?.let {
            header(HttpHeaders.Authorization, "Bearer $it")
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = ApiClient.client.post("${ApiClient.BASE_URL}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val response = ApiClient.client.post("${ApiClient.BASE_URL}/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(name, email, password))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSessions(): Result<List<ChatSession>> {
        return try {
            val response = ApiClient.client.get("${ApiClient.BASE_URL}/chats/sessions") {
                withAuth()
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSession(title: String): Result<ChatSession> {
        return try {
            val response = ApiClient.client.post("${ApiClient.BASE_URL}/chats/sessions") {
                withAuth()
                contentType(ContentType.Application.Json)
                setBody(CreateSessionRequest(title))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMessages(sessionId: String): Result<List<Message>> {
        return try {
            val response = ApiClient.client.get(
                "${ApiClient.BASE_URL}/chats/sessions/$sessionId/messages"
            ) { withAuth() }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(sessionId: String, message: String): Result<SendMessageResponse> {
        return try {
            val response = ApiClient.client.post(
                "${ApiClient.BASE_URL}/chats/sessions/$sessionId/messages"
            ) {
                withAuth()
                contentType(ContentType.Application.Json)
                setBody(SendMessageRequest(message))
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}