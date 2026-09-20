package com.communityconnect

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.communityconnect.presentation.viewmodel.MainViewModel
import com.communityconnect.ui.theme.Theme
import kotlinx.html.js.document
import org.w3c.dom.HTMLDivElement

@Composable
fun RootComponent() {
    val mainViewModel: MainViewModel = viewModel()
    Theme {
        App(mainViewModel = mainViewModel)
    }
}

fun main() {
    val root = document.getElementById("root") as? HTMLDivElement
    if (root != null) {
        val composeView = ComposeView(root)
        composeView.setContent {
            RootComponent()
        }
    }
}