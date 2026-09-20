package com.yallapark.ai

import com.yallapark.presentation.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatBubble(
    val id: String,
    val sender: String, // "user" or "assistant"
    val text: String,
    val timestamp: String = "Just now",
    val isLive: Boolean = false
)

class YallaAiViewModel(
    private val client: OpenRouterClient = OpenRouterClient(),
    private val testScope: CoroutineScope? = null
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatBubble>>(
        listOf(
            ChatBubble(
                id = "init_0",
                sender = "assistant",
                text = "Marhaba! I'm **YallaPark AI**, your Dubai smart parking assistant. Ask me about real-time availability in Bur Dubai, Karama, Deira, or Downtown, or find Women-Only Pink, POD, and Delivery Rider bays.",
                isLive = false
            )
        )
    )
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun sendMessage(userText: String) {
        if (userText.isBlank() || _isLoading.value) return

        val userBubble = ChatBubble(
            id = "msg_${userText.hashCode()}_${_messages.value.size}",
            sender = "user",
            text = userText
        )
        _messages.value = _messages.value + userBubble
        _isLoading.value = true

        val scope = testScope ?: viewModelScope
        scope.launch {
            // Filter out the system welcome greeting and enforce a bounded context window
            val history = _messages.value
                .filter { it.id != "init_0" }
                .takeLast(10)
                .map {
                    OpenRouterMessage(
                        role = if (it.sender == "user") "user" else "assistant",
                        content = it.text
                    )
                }

            val result = client.queryParkingAssistantDetailed(history)
            val conciergeResult = result.getOrElse {
                AiConciergeResult(
                    content = "Sorry, I encountered an issue retrieving real-time data. Please try again.",
                    isLive = false,
                    modelUsed = "error-fallback"
                )
            }

            val botBubble = ChatBubble(
                id = "bot_${conciergeResult.content.hashCode()}_${_messages.value.size}",
                sender = "assistant",
                text = conciergeResult.content,
                isLive = conciergeResult.isLive
            )
            _messages.value = _messages.value + botBubble
            _isLoading.value = false
        }
    }
}
