package com.yallapark.presentation.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

open class ViewModel {
    val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    open fun onCleared() {
        viewModelScope.cancel()
    }
}
