package com.communityconnect.ai

import ai.koog.agents.AIAgent
import ai.koog.agents.prompt.MultiLLMPromptExecutor
import ai.koog.openai.OpenAILLMClient
import ai.koog.openai.OpenAIModels

class AgentFactory(private val apiKey: String) {

    fun createAgent(): AIAgent {
        val client = OpenAILLMClient(
            apiKey = apiKey,
            baseUrl = "https://openrouter.ai/api/v1"
        )

        return AIAgent(
            promptExecutor = MultiLLMPromptExecutor(client),
            llmModel = OpenAIModels.Chat.GPT4o
        )
    }
}

@JvmStatic
fun createOpenRouterAgent(apiKey: String): AIAgent {
    return AgentFactory(apiKey).createAgent()
}