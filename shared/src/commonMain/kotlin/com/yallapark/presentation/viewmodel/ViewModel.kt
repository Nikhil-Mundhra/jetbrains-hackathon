package com.yallapark.presentation.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

open class ViewModel(
    customScope: CoroutineScope? = null
) {
    val viewModelScope: CoroutineScope = customScope ?: defaultScope()

    open fun onCleared() {
        viewModelScope.cancel()
    }

    private companion object {
        private fun defaultScope(): CoroutineScope {
            val dispatcher = try {
                // Accessing .immediate on MissingMainCoroutineDispatcher throws immediately,
                // allowing safe fallback to Dispatchers.Default in headless testing environments.
                Dispatchers.Main.immediate
                Dispatchers.Main
            } catch (_: Throwable) {
                Dispatchers.Default
            }
            return CoroutineScope(dispatcher + SupervisorJob())
        }
    }
}
