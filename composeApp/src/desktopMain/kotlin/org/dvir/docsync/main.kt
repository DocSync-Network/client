package org.dvir.docsync

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.dvir.docsync.di.appModule
import org.koin.core.context.GlobalContext.startKoin

fun main() = application {
    startKoin {
        modules(appModule)
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Docsync",
    ) {
        App()
    }
}