package com.communityconnect

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.singleWindowApplication
import androidx.lifecycle.viewmodel.compose.viewModel
import com.communityconnect.presentation.viewmodel.MainViewModel
import com.communityconnect.ui.theme.Theme

fun main() = singleWindowApplication {
    val mainViewModel: MainViewModel = viewModel()
    MaterialTheme {
        Theme {
            App(mainViewModel = mainViewModel)
        }
    }
}