package com.communityconnect

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.yallapark.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "YallaPark — Smart Mobility & Predictive Parking Platform"
    ) {
        App()
    }
}