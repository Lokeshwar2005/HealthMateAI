package com.example.healthmateai.ai.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthmateai.network.openrouter.OpenRouterMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatBubble(
    val id: Long,
    val text: String,
    val isUser: Boolean
)

data class ChatbotUiState(
    val messages: List<ChatBubble> = emptyList(),
    val isTyping: Boolean = false,
    val error: String? = null
)

class ChatbotViewModel(
    private val repository: ChatbotRepository = ChatbotRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatbotUiState())
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    private val history = mutableListOf(
        OpenRouterMessage(
            role = "system",
            content = "You are HealthMate AI, a concise healthcare assistant. Keep answers practical and short. Focus on diet advice, wellness tips, interpreting risk predictions, and symptom triage guidance. Avoid definitive diagnosis and suggest consulting clinicians for urgent/severe symptoms."
        )
    )

    fun send(message: String) {
        val prompt = message.trim()
        if (prompt.isBlank()) return

        val userBubble = ChatBubble(id = System.currentTimeMillis(), text = prompt, isUser = true)
        _uiState.update {
            it.copy(messages = it.messages + userBubble, isTyping = true, error = null)
        }
        history += OpenRouterMessage(role = "user", content = prompt)

        viewModelScope.launch {
            runCatching { repository.ask(history.toList()) }
                .onSuccess { reply ->
                    history += OpenRouterMessage(role = "assistant", content = reply)
                    _uiState.update {
                        it.copy(
                            messages = it.messages + ChatBubble(
                                id = System.currentTimeMillis() + 1,
                                text = reply,
                                isUser = false
                            ),
                            isTyping = false,
                            error = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isTyping = false, error = repository.toReadableError(throwable))
                    }
                }
        }
    }
}

object ChatbotViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatbotViewModel() as T
    }
}
