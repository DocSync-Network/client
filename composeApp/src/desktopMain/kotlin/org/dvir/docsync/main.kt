package org.dvir.docsync

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    ServiceLocator.setup()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Docsync",
    ) {
        App()
    }
}