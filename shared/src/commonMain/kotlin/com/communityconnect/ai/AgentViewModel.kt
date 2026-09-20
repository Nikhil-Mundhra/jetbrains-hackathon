package com.communityconnect.ai

import com.yallapark.presentation.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AgentViewModel(private val apiKey: String) : ViewModel() {

    private val _response = MutableStateFlow<String?>(null)
    val response = _response.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val agent = AgentFactory(apiKey).createAgent()

    fun sendMessage(message: String) {
        _isLoading.value = true
        _error.value = null
        _response.value = null

        viewModelScope.launch {
            try {
                val result = agent.run(message)
                _response.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}