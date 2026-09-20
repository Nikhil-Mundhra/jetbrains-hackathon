package com.communityconnect

import androidx.compose.ui.window.ComposeUIViewController
import ios.UIKit.UIViewController
import kotlin.ui.platform.ViewController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.communityconnect.presentation.viewmodel.MainViewModel
import com.communityconnect.ui.theme.Theme

fun MainViewController(): UIViewController = ComposeUIViewController {
    val mainViewModel: MainViewModel = viewModel()
    Theme {
        App(mainViewModel = mainViewModel)
    }
}