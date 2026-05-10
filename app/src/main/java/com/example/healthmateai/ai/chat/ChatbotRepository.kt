package com.example.healthmateai.ai.chat

import com.example.healthmateai.BuildConfig
import com.example.healthmateai.network.openrouter.OpenRouterApiClient
import com.example.healthmateai.network.openrouter.OpenRouterMessage
import com.example.healthmateai.network.openrouter.OpenRouterRequest
import com.example.healthmateai.network.openrouter.firstMessageContent
import com.example.healthmateai.network.openrouter.openRouterAuthHeader
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ChatbotRepository {

    suspend fun ask(messages: List<OpenRouterMessage>): String {
        val request = OpenRouterRequest(
            model = BuildConfig.OPENROUTER_MODEL,
            messages = messages,
            temperature = 0.4,
            maxTokens = 420
        )

        val response = OpenRouterApiClient.service.createChatCompletion(
            authorization = openRouterAuthHeader(),
            request = request
        )

        return response.firstMessageContent().ifBlank {
            throw IllegalStateException("AI returned an empty answer")
        }
    }

    fun toReadableError(error: Throwable): String {
        return when (error) {
            is UnknownHostException -> "No internet connection"
            is SocketTimeoutException -> "The AI took too long to respond"
            else -> error.message ?: "Unable to get response"
        }
    }
}
