package com.communityconnect

import androidx.compose.ui.window.ComposeUIViewController
import com.yallapark.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    App()
}