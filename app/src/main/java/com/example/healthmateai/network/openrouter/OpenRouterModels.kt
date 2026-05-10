package com.example.healthmateai.network.openrouter

import com.google.gson.annotations.SerializedName

data class OpenRouterRequest(
    val model: String,
    val messages: List<OpenRouterMessage>,
    val temperature: Double = 0.4,
    @SerializedName("max_tokens")
    val maxTokens: Int = 700
)

data class OpenRouterMessage(
    val role: String,
    val content: String
)

data class OpenRouterResponse(
    val choices: List<OpenRouterChoice> = emptyList()
)

data class OpenRouterChoice(
    val message: OpenRouterMessageContent? = null
)

data class OpenRouterMessageContent(
    val content: String? = null
)

fun OpenRouterResponse.firstMessageContent(): String {
    return choices.firstOrNull()?.message?.content.orEmpty().trim()
}
