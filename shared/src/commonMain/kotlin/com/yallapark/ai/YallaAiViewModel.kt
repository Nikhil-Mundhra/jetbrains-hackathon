package com.yallapark.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatBubble(
    val id: String,
    val sender: String, // "user" or "assistant"
    val text: String,
    val timestamp: String = "Just now"
)

class YallaAiViewModel(
    private val client: OpenRouterClient = OpenRouterClient()
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatBubble>>(
        listOf(
            ChatBubble(
                id = "init_0",
                sender = "assistant",
                text = "Marhaba! I'm **YallaPark AI**, your Dubai smart parking assistant. Ask me about real-time availability in Bur Dubai, Karama, Deira, or Downtown, or find Women-Only Pink, POD, and Delivery Rider bays."
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

        viewModelScope.launch {
            val history = _messages.value.map {
                OpenRouterMessage(
                    role = if (it.sender == "user") "user" else "assistant",
                    content = it.text
                )
            }

            val result = client.queryParkingAssistant(history)
            val replyText = result.getOrElse {
                "Sorry, I encountered an issue retrieving real-time data. Please try again."
            }

            val botBubble = ChatBubble(
                id = "bot_${replyText.hashCode()}_${_messages.value.size}",
                sender = "assistant",
                text = replyText
            )
            _messages.value = _messages.value + botBubble
            _isLoading.value = false
        }
    }
}
